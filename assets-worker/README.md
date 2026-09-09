# Zipline assets worker

This Worker serves the signed Zipline parser release assets that the Android app loads remotely. Each Zipline ABI has its own immutable R2 release tree:

```text
releases/<Zipline ABI version>/<UTC timestamp>/
├── manifest.zipline.json
└── *.zipline
```

The latest version is recorded into a Workers KV namespace with ABI versions stamped into the key.

```text
current/v2 = <UTC timestamp>
current/v3 = <UTC timestamp>
```

`GET` and `HEAD` requests support any ABI version segment such as `v2` or `v3`:

- `/current/<Zipline ABI version>/manifest.zipline.json` and `/current/<Zipline ABI version>/<module>.zipline`, resolved through that ABI's KV pointer. These responses use `Cache-Control: no-store` so an activation is observed on the next request.
- `/current/<Zipline ABI version>`, which returns the active release as JSON: `{"releaseTimestamp":"<UTC timestamp>","ziplineVersion":"vN"}`.
- `/releases/<Zipline ABI version>/<UTC timestamp>/manifest.zipline.json` and `/releases/<Zipline ABI version>/<UTC timestamp>/<module>.zipline`, resolved directly from R2. These immutable paths use a one-year cache lifetime.

## Publishing a release

`POST /admin/releases` accepts the flat ZIP archive created by `scripts/release-zipline-parser.sh`. The raw `application/zip` body must contain `manifest.zipline.json` and at least one top-level `.zipline` module. Archives larger than 10 MiB, that expand beyond 32 MiB, or that contain more than 20 modules are rejected.

The endpoint uses the required `ADMIN_PASSWORD` Worker secret as a Bearer credential. Set it once, interactively, before the first Worker deployment:

```sh
pnpm dlx wrangler secret put ADMIN_PASSWORD
```

Publish an ABI v2 archive:

```sh
curl --fail --request POST https://claw.msfjarvis.dev/admin/releases \
  --header "Authorization: Bearer $ADMIN_PASSWORD" \
  --header "Content-Type: application/zip" \
  --header "X-Zipline-Version: v2" \
  --data-binary @zipline-release.zip
```

For the next ABI break, upload to the same versionless admin endpoint with `X-Zipline-Version: v3`. The release becomes available at `/current/v3/...`.

The Worker validates the entire archive before writing. It serializes uploads for each ABI, uploads modules, uploads the manifest last, and updates that ABI's `current/vN` key only after every R2 write succeeds. A successful `201` response includes the generated timestamp and ABI version. Failed archive validation leaves the active release unchanged; incomplete, unactivated R2 objects are harmless.

## Rolling back a release

`POST /admin/rollback` reactivates an existing release for one ABI. Supply the timestamp returned when that release was uploaded. The Worker verifies that the release manifest exists before changing the `current/vN` pointer, then returns the active timestamp and ABI version as JSON.

```sh
curl --fail --request POST https://claw.msfjarvis.dev/admin/rollback \
  --header "Authorization: Bearer $ADMIN_PASSWORD" \
  --header "Content-Type: application/json" \
  --header "X-Zipline-Version: v2" \
  --data '{"releaseTimestamp":"20260830T000000Z"}'
```

The Worker configuration already binds the existing R2 bucket and KV namespace. Deploy it with:

```sh
pnpm ci
pnpm test
pnpm dlx wrangler deploy
```
