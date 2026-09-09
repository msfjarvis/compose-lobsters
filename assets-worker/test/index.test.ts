import { env, exports } from "cloudflare:workers";
import { strToU8, zipSync } from "fflate/browser";
import { beforeEach, describe, expect, it } from "vitest";

const RELEASE_TIMESTAMP = "20260830T000000Z";
const PREVIOUS_RELEASE_TIMESTAMP = "20260829T000000Z";
const RELEASE_KEY = `releases/v2/${RELEASE_TIMESTAMP}`;
const ADMIN_PASSWORD = "test-admin-password";

function zipRelease(files: Record<string, string>): Uint8Array {
  return zipSync(
    Object.fromEntries(Object.entries(files).map(([name, body]) => [name, strToU8(body)])),
  );
}

function adminRequest(
  files: Record<string, string>,
  ziplineVersion = "v3",
): RequestInit {
  const body = zipRelease(files);
  return {
    method: "POST",
    headers: {
      Authorization: `Bearer ${ADMIN_PASSWORD}`,
      "Content-Type": "application/zip",
      "Content-Length": String(body.byteLength),
      "X-Zipline-Version": ziplineVersion,
    },
    body,
  };
}

function rollbackRequest(
  releaseTimestamp: string,
  ziplineVersion = "v3",
): RequestInit {
  return {
    method: "POST",
    headers: {
      Authorization: `Bearer ${ADMIN_PASSWORD}`,
      "Content-Type": "application/json",
      "X-Zipline-Version": ziplineVersion,
    },
    body: JSON.stringify({ releaseTimestamp }),
  };
}

beforeEach(async () => {
  const objects = await env.ASSETS.list();
  if (objects.truncated) {
    throw new Error("Test cleanup must not leave more than 1,000 R2 objects");
  }
  await env.ASSETS.delete(objects.objects.map((object) => object.key));
  await env.RELEASES.delete("current/v2");
  await env.RELEASES.delete("current/v3");
});

describe("Zipline asset worker", () => {
  it("resolves current assets through the KV release pointer", async () => {
    await env.ASSETS.put(`${RELEASE_KEY}/manifest.zipline.json`, "manifest", {
      httpMetadata: { contentType: "application/json" },
    });
    await env.RELEASES.put("current/v2", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/current/v2/manifest.zipline.json",
    );

    expect(response.status).toBe(200);
    expect(await response.text()).toBe("manifest");
    expect(response.headers.get("Cache-Control")).toBe("no-store");
    expect(response.headers.get("Content-Type")).toBe("application/json");
  });

  it("returns the current release timestamp for an ABI", async () => {
    await env.RELEASES.put("current/v2", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch("https://assets.example/current/v2");

    expect(response.status).toBe(200);
    expect(await response.json()).toEqual({
      releaseTimestamp: RELEASE_TIMESTAMP,
      ziplineVersion: "v2",
    });
    expect(response.headers.get("Cache-Control")).toBe("no-store");
    expect(response.headers.get("Content-Type")).toBe("application/json");
  });

  it("returns JSON metadata headers without a body for HEAD current-release requests", async () => {
    await env.RELEASES.put("current/v2", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch("https://assets.example/current/v2", {
      method: "HEAD",
    });

    expect(response.status).toBe(200);
    expect(response.headers.get("Content-Type")).toBe("application/json");
    expect(response.headers.get("Content-Length")).toBe(
      String(
        new TextEncoder().encode(
          JSON.stringify({ releaseTimestamp: RELEASE_TIMESTAMP, ziplineVersion: "v2" }),
        ).byteLength,
      ),
    );
    expect(await response.text()).toBe("");
  });

  it("serves immutable release URLs directly from R2", async () => {
    await env.ASSETS.put(`${RELEASE_KEY}/zipline-parser.zipline`, "module");

    const response = await exports.default.fetch(
      `https://assets.example/releases/v2/${RELEASE_TIMESTAMP}/zipline-parser.zipline`,
    );

    expect(response.status).toBe(200);
    expect(new TextDecoder().decode(await response.arrayBuffer())).toBe("module");
    expect(response.headers.get("Cache-Control")).toBe(
      "public, max-age=31536000, immutable",
    );
    expect(response.headers.get("Content-Type")).toBe("application/octet-stream");
  });

  it("returns metadata without reading an object body for HEAD requests", async () => {
    await env.ASSETS.put(`${RELEASE_KEY}/zipline-parser.zipline`, "module");
    await env.RELEASES.put("current/v2", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/current/v2/zipline-parser.zipline",
      { method: "HEAD" },
    );

    expect(response.status).toBe(200);
    expect(response.headers.get("Content-Length")).toBe("6");
    expect(await response.text()).toBe("");
  });

  it("returns 503 when no current release is configured", async () => {
    const response = await exports.default.fetch(
      "https://assets.example/current/v2/manifest.zipline.json",
    );

    expect(response.status).toBe(503);
  });

  it("returns 503 when no release timestamp is available for an ABI", async () => {
    const response = await exports.default.fetch("https://assets.example/current/v2");

    expect(response.status).toBe(503);
  });

  it("returns 503 when the current-release pointer is invalid", async () => {
    await env.RELEASES.put("current/v2", "not-a-release-timestamp");

    const response = await exports.default.fetch(
      "https://assets.example/current/v2/manifest.zipline.json",
    );

    expect(response.status).toBe(503);
  });

  it("returns 404 for a missing asset", async () => {
    await env.RELEASES.put("current/v2", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/current/v2/manifest.zipline.json",
      { method: "HEAD" },
    );

    expect(response.status).toBe(404);
  });

  it("does not expose arbitrary R2 keys", async () => {
    const response = await exports.default.fetch(
      "https://assets.example/current/v2/../../private.txt",
    );

    expect(response.status).toBe(404);
  });

  it("accepts only GET and HEAD for public assets", async () => {
    const response = await exports.default.fetch(
      "https://assets.example/current/v2/manifest.zipline.json",
      { method: "POST" },
    );

    expect(response.status).toBe(405);
    expect(response.headers.get("Allow")).toBe("GET, HEAD");
  });
});

describe("Zipline release administration", () => {
  it("publishes a v3 archive through the versionless admin API", async () => {
    const response = await exports.default.fetch(
      "https://assets.example/admin/releases",
      adminRequest({
        "manifest.zipline.json": "{}",
        "zipline-parser.zipline": "module",
      }),
    );

    expect(response.status).toBe(201);
    const { releaseTimestamp, ziplineVersion } = await response.json<{
      releaseTimestamp: string;
      ziplineVersion: string;
    }>();
    expect(releaseTimestamp).toMatch(/^\d{8}T\d{6}Z$/);
    expect(ziplineVersion).toBe("v3");
    expect(await env.RELEASES.get("current/v3")).toBe(releaseTimestamp);

    const currentManifest = await exports.default.fetch(
      "https://assets.example/current/v3/manifest.zipline.json",
    );
    const module = await env.ASSETS.get(
      `releases/v3/${releaseTimestamp}/zipline-parser.zipline`,
    );
    expect(await currentManifest.text()).toBe("{}");
    expect(new TextDecoder().decode(await module?.arrayBuffer())).toBe("module");
    expect(module?.httpMetadata?.contentType).toBe("application/octet-stream");
  });

  it("rejects an incorrect bearer password without writing a release", async () => {
    const request = adminRequest({
      "manifest.zipline.json": "{}",
      "zipline-parser.zipline": "module",
    });
    const headers = new Headers(request.headers);
    headers.set("Authorization", "Bearer incorrect");

    const response = await exports.default.fetch("https://assets.example/admin/releases", {
      ...request,
      headers,
    });

    expect(response.status).toBe(401);
    expect(response.headers.get("WWW-Authenticate")).toBe("Bearer");
    expect(await env.RELEASES.get("current/v3")).toBeNull();
    expect((await env.ASSETS.list()).objects).toHaveLength(0);
  });

  it("does not activate an invalid archive", async () => {
    await env.RELEASES.put("current/v3", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/admin/releases",
      adminRequest({ "manifest.zipline.json": "{}" }),
    );

    expect(response.status).toBe(400);
    expect(await env.RELEASES.get("current/v3")).toBe(RELEASE_TIMESTAMP);
    expect((await env.ASSETS.list()).objects).toHaveLength(0);
  });

  it("requires an explicit Zipline ABI version", async () => {
    const request = adminRequest({
      "manifest.zipline.json": "{}",
      "zipline-parser.zipline": "module",
    });
    const headers = new Headers(request.headers);
    headers.delete("X-Zipline-Version");

    const response = await exports.default.fetch("https://assets.example/admin/releases", {
      ...request,
      headers,
    });

    expect(response.status).toBe(400);
    expect(await env.RELEASES.get("current/v3")).toBeNull();
  });

  it("accepts only POST requests", async () => {
    const response = await exports.default.fetch("https://assets.example/admin/releases");

    expect(response.status).toBe(405);
    expect(response.headers.get("Allow")).toBe("POST");
  });

  it("rolls an ABI back to an existing release", async () => {
    await env.ASSETS.put(
      `releases/v3/${PREVIOUS_RELEASE_TIMESTAMP}/manifest.zipline.json`,
      "{}",
    );
    await env.RELEASES.put("current/v3", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/admin/rollback",
      rollbackRequest(PREVIOUS_RELEASE_TIMESTAMP),
    );

    expect(response.status).toBe(200);
    expect(await response.json()).toEqual({
      releaseTimestamp: PREVIOUS_RELEASE_TIMESTAMP,
      ziplineVersion: "v3",
    });
    expect(await env.RELEASES.get("current/v3")).toBe(PREVIOUS_RELEASE_TIMESTAMP);
  });

  it("does not activate an unknown release during rollback", async () => {
    await env.RELEASES.put("current/v3", RELEASE_TIMESTAMP);

    const response = await exports.default.fetch(
      "https://assets.example/admin/rollback",
      rollbackRequest(PREVIOUS_RELEASE_TIMESTAMP),
    );

    expect(response.status).toBe(404);
    expect(await env.RELEASES.get("current/v3")).toBe(RELEASE_TIMESTAMP);
  });

  it("rejects unauthorized rollback requests without changing the active release", async () => {
    await env.ASSETS.put(
      `releases/v3/${PREVIOUS_RELEASE_TIMESTAMP}/manifest.zipline.json`,
      "{}",
    );
    await env.RELEASES.put("current/v3", RELEASE_TIMESTAMP);
    const request = rollbackRequest(PREVIOUS_RELEASE_TIMESTAMP);
    const headers = new Headers(request.headers);
    headers.set("Authorization", "Bearer incorrect");

    const response = await exports.default.fetch("https://assets.example/admin/rollback", {
      ...request,
      headers,
    });

    expect(response.status).toBe(401);
    expect(response.headers.get("WWW-Authenticate")).toBe("Bearer");
    expect(await env.RELEASES.get("current/v3")).toBe(RELEASE_TIMESTAMP);
  });
});
