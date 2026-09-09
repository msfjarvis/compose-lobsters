import { Unzip, UnzipInflate } from "fflate/browser";

const ADMIN_RELEASE_PATH = "/admin/releases";
const ADMIN_ROLLBACK_PATH = "/admin/rollback";
const CURRENT_CACHE_CONTROL = "no-store";
const IMMUTABLE_CACHE_CONTROL = "public, max-age=31536000, immutable";
const MAX_ARCHIVE_BYTES = 10 * 1024 * 1024;
const MAX_MODULES = 20;
const MAX_UNCOMPRESSED_BYTES = 32 * 1024 * 1024;
const RELEASE_TIMESTAMP = /^\d{8}T\d{6}Z$/;
const ZIPLINE_VERSION = /^v[1-9]\d*$/;
const ASSET_NAME = /^(?:manifest\.zipline\.json|[A-Za-z0-9][A-Za-z0-9._-]*\.zipline)$/;

type AssetRequest = {
  assetName: string;
  cacheControl: string;
  releaseTimestamp: string;
  ziplineVersion: string;
};

type ReleaseArchive = {
  manifest: Uint8Array;
  modules: ReadonlyArray<readonly [string, Uint8Array]>;
};

class BadRequestError extends Error {}
class ConflictError extends Error {}

function parseAssetRequest(pathname: string): AssetRequest | null {
  const segments = pathname.split("/");

  if (
    segments.length === 4 &&
    segments[0] === "" &&
    segments[1] === "current" &&
    ZIPLINE_VERSION.test(segments[2]) &&
    ASSET_NAME.test(segments[3])
  ) {
    return {
      assetName: segments[3],
      cacheControl: CURRENT_CACHE_CONTROL,
      releaseTimestamp: "",
      ziplineVersion: segments[2],
    };
  }

  if (
    segments.length === 5 &&
    segments[0] === "" &&
    segments[1] === "releases" &&
    ZIPLINE_VERSION.test(segments[2]) &&
    RELEASE_TIMESTAMP.test(segments[3]) &&
    ASSET_NAME.test(segments[4])
  ) {
    return {
      assetName: segments[4],
      cacheControl: IMMUTABLE_CACHE_CONTROL,
      releaseTimestamp: segments[3],
      ziplineVersion: segments[2],
    };
  }

  return null;
}

function parseCurrentReleaseVersion(pathname: string): string | null {
  const segments = pathname.split("/");
  return segments.length === 3 && segments[0] === "" && segments[1] === "current" &&
      ZIPLINE_VERSION.test(segments[2])
    ? segments[2]
    : null;
}

function currentReleaseKey(ziplineVersion: string): string {
  return `current/${ziplineVersion}`;
}

function releasePrefix(ziplineVersion: string, releaseTimestamp: string): string {
  return `releases/${ziplineVersion}/${releaseTimestamp}`;
}

function objectKey(
  ziplineVersion: string,
  releaseTimestamp: string,
  assetName: string,
): string {
  return `${releasePrefix(ziplineVersion, releaseTimestamp)}/${assetName}`;
}

function contentType(assetName: string): string {
  return assetName === "manifest.zipline.json"
    ? "application/json"
    : "application/octet-stream";
}

function responseHeaders(
  object: R2Object,
  assetName: string,
  cacheControl: string,
): Headers {
  const headers = new Headers();
  object.writeHttpMetadata(headers);
  headers.set("Cache-Control", cacheControl);
  headers.set("Content-Length", String(object.size));
  headers.set("Content-Type", headers.get("Content-Type") ?? contentType(assetName));
  headers.set("ETag", object.httpEtag);
  headers.set("Last-Modified", object.uploaded.toUTCString());
  return headers;
}

function currentTimestamp(): string {
  return new Date()
    .toISOString()
    .replace(/[-:]/g, "")
    .replace(/\.\d{3}Z$/, "Z");
}

function joinChunks(chunks: readonly Uint8Array[], size: number): Uint8Array {
  const output = new Uint8Array(size);
  let offset = 0;
  for (const chunk of chunks) {
    output.set(chunk, offset);
    offset += chunk.byteLength;
  }
  return output;
}

function parseArchive(archive: Uint8Array): ReleaseArchive {
  const files = new Map<string, Uint8Array>();
  let totalUncompressedBytes = 0;
  let moduleCount = 0;
  const unzipper = new Unzip((file) => {
    if (!ASSET_NAME.test(file.name)) {
      throw new BadRequestError(
        "The ZIP archive must contain only top-level Zipline modules and manifest.zipline.json",
      );
    }
    const originalSize = file.originalSize;
    if (originalSize === undefined || originalSize > MAX_UNCOMPRESSED_BYTES) {
      throw new BadRequestError("The ZIP archive contains an oversized or unsupported entry");
    }
    if (file.name.endsWith(".zipline") && ++moduleCount > MAX_MODULES) {
      throw new BadRequestError(`The ZIP archive cannot contain more than ${MAX_MODULES} modules`);
    }

    const chunks: Uint8Array[] = [];
    let size = 0;
    file.ondata = (error, chunk, final) => {
      if (error) {
        throw new BadRequestError("The ZIP archive could not be decompressed");
      }

      size += chunk.byteLength;
      totalUncompressedBytes += chunk.byteLength;
      if (size > originalSize || totalUncompressedBytes > MAX_UNCOMPRESSED_BYTES) {
        throw new BadRequestError("The ZIP archive expands beyond the 32 MiB limit");
      }
      chunks.push(chunk);

      if (final) {
        files.set(file.name, joinChunks(chunks, size));
      }
    };
    file.start();
  });
  unzipper.register(UnzipInflate);

  try {
    unzipper.push(archive, true);
  } catch (error) {
    if (error instanceof BadRequestError) {
      throw error;
    }
    throw new BadRequestError("The request body is not a valid ZIP archive");
  }

  const manifest = files.get("manifest.zipline.json");
  const modules = [...files].filter(([name]) => name.endsWith(".zipline"));
  if (!manifest || modules.length === 0) {
    throw new BadRequestError(
      "The ZIP archive must contain manifest.zipline.json and at least one .zipline module",
    );
  }

  try {
    JSON.parse(new TextDecoder().decode(manifest));
  } catch {
    throw new BadRequestError("manifest.zipline.json must contain valid JSON");
  }

  return { manifest, modules };
}

async function isAuthorized(request: Request, env: Env): Promise<boolean> {
  const token = request.headers.get("Authorization")?.match(/^Bearer (.+)$/i)?.[1];
  if (!token || !env.ADMIN_PASSWORD) {
    return false;
  }

  const encoder = new TextEncoder();
  const [tokenDigest, passwordDigest] = await Promise.all([
    crypto.subtle.digest("SHA-256", encoder.encode(token)),
    crypto.subtle.digest("SHA-256", encoder.encode(env.ADMIN_PASSWORD)),
  ]);
  return crypto.subtle.timingSafeEqual(tokenDigest, passwordDigest);
}

async function resolveReleaseTimestamp(
  assetRequest: AssetRequest,
  env: Env,
): Promise<string | null> {
  if (assetRequest.releaseTimestamp) {
    return assetRequest.releaseTimestamp;
  }

  const releaseTimestamp = await env.RELEASES.get(
    currentReleaseKey(assetRequest.ziplineVersion),
  );
  return releaseTimestamp && RELEASE_TIMESTAMP.test(releaseTimestamp)
    ? releaseTimestamp
    : null;
}

async function handleCurrentReleaseRequest(
  request: Request,
  env: Env,
  ziplineVersion: string,
): Promise<Response> {
  const releaseTimestamp = await env.RELEASES.get(currentReleaseKey(ziplineVersion));
  if (!releaseTimestamp || !RELEASE_TIMESTAMP.test(releaseTimestamp)) {
    return new Response("Current release is unavailable", { status: 503 });
  }

  const body = JSON.stringify({ releaseTimestamp, ziplineVersion });
  const headers = new Headers({
    "Cache-Control": CURRENT_CACHE_CONTROL,
    "Content-Length": String(new TextEncoder().encode(body).byteLength),
    "Content-Type": "application/json",
  });
  return request.method === "HEAD"
    ? new Response(null, { headers })
    : new Response(body, { headers });
}

async function handleAssetRequest(
  request: Request,
  env: Env,
  assetRequest: AssetRequest,
): Promise<Response> {
  const releaseTimestamp = await resolveReleaseTimestamp(assetRequest, env);
  if (!releaseTimestamp) {
    return new Response("Current release is unavailable", { status: 503 });
  }

  const key = objectKey(
    assetRequest.ziplineVersion,
    releaseTimestamp,
    assetRequest.assetName,
  );
  if (request.method === "HEAD") {
    const object = await env.ASSETS.head(key);
    return object
      ? new Response(null, {
          headers: responseHeaders(object, assetRequest.assetName, assetRequest.cacheControl),
        })
      : new Response("Not found", { status: 404 });
  }

  const object = await env.ASSETS.get(key);
  return object
    ? new Response(object.body, {
        headers: responseHeaders(object, assetRequest.assetName, assetRequest.cacheControl),
      })
    : new Response("Not found", { status: 404 });
}

function publishingLockKey(ziplineVersion: string): string {
  return `.publishing/${ziplineVersion}`;
}

async function reserveReleaseUpdate(env: Env, ziplineVersion: string): Promise<void> {
  const lock = await env.ASSETS.put(publishingLockKey(ziplineVersion), "", {
    onlyIf: new Headers({ "If-None-Match": "*" }),
  });
  if (!lock) {
    throw new ConflictError("A release is already being published for this Zipline ABI");
  }
}

async function rollbackReleaseTimestamp(request: Request): Promise<string> {
  let body: unknown;
  try {
    body = await request.json();
  } catch {
    throw new BadRequestError("The request body must be valid JSON");
  }

  if (
    typeof body !== "object" ||
    body === null ||
    !("releaseTimestamp" in body) ||
    typeof body.releaseTimestamp !== "string" ||
    !RELEASE_TIMESTAMP.test(body.releaseTimestamp)
  ) {
    throw new BadRequestError("releaseTimestamp must be a UTC timestamp such as 20260830T000000Z");
  }
  return body.releaseTimestamp;
}

async function handleAdminRelease(request: Request, env: Env): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("Method not allowed", {
      status: 405,
      headers: { Allow: "POST" },
    });
  }

  if (!(await isAuthorized(request, env))) {
    return new Response("Unauthorized", {
      status: 401,
      headers: { "WWW-Authenticate": "Bearer" },
    });
  }

  const ziplineVersion = request.headers.get("X-Zipline-Version");
  if (!ziplineVersion || !ZIPLINE_VERSION.test(ziplineVersion)) {
    throw new BadRequestError("X-Zipline-Version must be a version such as v2 or v3");
  }

  const contentLengthHeader = request.headers.get("Content-Length");
  if (contentLengthHeader === null) {
    throw new BadRequestError("Content-Length is required");
  }
  const contentLength = Number(contentLengthHeader);
  if (
    !Number.isSafeInteger(contentLength) ||
    contentLength < 0 ||
    contentLength > MAX_ARCHIVE_BYTES
  ) {
    throw new BadRequestError("Content-Length must be a ZIP size no greater than 10 MiB");
  }

  const archiveBytes = new Uint8Array(await request.arrayBuffer());
  if (archiveBytes.byteLength > MAX_ARCHIVE_BYTES || archiveBytes.byteLength !== contentLength) {
    throw new BadRequestError("The request body does not match the permitted Content-Length");
  }

  const archive = parseArchive(archiveBytes);
  await reserveReleaseUpdate(env, ziplineVersion);
  try {
    const releaseTimestamp = currentTimestamp();
    await Promise.all(
      archive.modules.map(([assetName, body]) =>
        env.ASSETS.put(objectKey(ziplineVersion, releaseTimestamp, assetName), body, {
          httpMetadata: { contentType: contentType(assetName) },
        }),
      ),
    );
    await env.ASSETS.put(
      objectKey(ziplineVersion, releaseTimestamp, "manifest.zipline.json"),
      archive.manifest,
      { httpMetadata: { contentType: contentType("manifest.zipline.json") } },
    );
    await env.RELEASES.put(currentReleaseKey(ziplineVersion), releaseTimestamp);

    return Response.json({ releaseTimestamp, ziplineVersion }, { status: 201 });
  } finally {
    await env.ASSETS.delete(publishingLockKey(ziplineVersion));
  }
}

async function handleAdminRollback(request: Request, env: Env): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("Method not allowed", {
      status: 405,
      headers: { Allow: "POST" },
    });
  }

  if (!(await isAuthorized(request, env))) {
    return new Response("Unauthorized", {
      status: 401,
      headers: { "WWW-Authenticate": "Bearer" },
    });
  }

  const ziplineVersion = request.headers.get("X-Zipline-Version");
  if (!ziplineVersion || !ZIPLINE_VERSION.test(ziplineVersion)) {
    throw new BadRequestError("X-Zipline-Version must be a version such as v2 or v3");
  }

  const releaseTimestamp = await rollbackReleaseTimestamp(request);
  const manifest = await env.ASSETS.head(
    objectKey(ziplineVersion, releaseTimestamp, "manifest.zipline.json"),
  );
  if (!manifest) {
    return new Response("Release not found", { status: 404 });
  }

  await reserveReleaseUpdate(env, ziplineVersion);
  try {
    await env.RELEASES.put(currentReleaseKey(ziplineVersion), releaseTimestamp);
    return Response.json({ releaseTimestamp, ziplineVersion });
  } finally {
    await env.ASSETS.delete(publishingLockKey(ziplineVersion));
  }
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    try {
      const url = new URL(request.url);
      if (url.pathname === ADMIN_RELEASE_PATH) {
        return await handleAdminRelease(request, env);
      }
      if (url.pathname === ADMIN_ROLLBACK_PATH) {
        return await handleAdminRollback(request, env);
      }

      if (request.method !== "GET" && request.method !== "HEAD") {
        return new Response("Method not allowed", {
          status: 405,
          headers: { Allow: "GET, HEAD" },
        });
      }

      const currentReleaseVersion = parseCurrentReleaseVersion(url.pathname);
      if (currentReleaseVersion) {
        return await handleCurrentReleaseRequest(request, env, currentReleaseVersion);
      }

      const assetRequest = parseAssetRequest(url.pathname);
      if (!assetRequest) {
        return new Response("Not found", { status: 404 });
      }

      return await handleAssetRequest(request, env, assetRequest);
    } catch (error) {
      if (error instanceof BadRequestError) {
        return new Response(error.message, { status: 400 });
      }
      if (error instanceof ConflictError) {
        return new Response(error.message, { status: 409 });
      }

      console.error(
        JSON.stringify({
          error: error instanceof Error ? error.message : String(error),
          message: "Unable to serve or publish a Zipline asset",
          url: request.url,
        }),
      );
      return new Response("Internal server error", { status: 500 });
    }
  },
} satisfies ExportedHandler<Env>;
