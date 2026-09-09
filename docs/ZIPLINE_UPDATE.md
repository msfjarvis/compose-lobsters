# Releasing the Zipline parser

The production build remains signed with `ZIPLINE_SIGNING_KEY`, but releases are uploaded directly to the assets Worker instead of `wailord`.

1. Set `ZIPLINE_SIGNING_KEY`.
2. Build and test the production bundle:

   ```sh
   ./gradlew :zipline-parser:jsTest :zipline-parser:compileProductionExecutableKotlinJsZipline
   ```

3. Create a flat archive from `zipline-parser/build/zipline/Production/`:

   ```sh
   pushd zipline-parser/build/zipline/Production
   zip -r -j ../../../../zipline-release.zip .
   popd
   ```

   The archive must contain `manifest.zipline.json` and at least one `.zipline` module at its root.

4. Upload and activate it with the Worker secret configured as `ADMIN_PASSWORD`:

   ```sh
   curl --fail --request POST https://claw.msfjarvis.dev/admin/releases \
     --header "Authorization: Bearer $ADMIN_PASSWORD" \
     --header "Content-Type: application/zip" \
     --header "X-Zipline-Version: v2" \
     --data-binary @zipline-release.zip
   ```

`/admin/releases` is independent of the Zipline ABI. For a breaking Zipline ABI update, use the same endpoint with `X-Zipline-Version: v3`; the Worker writes the new release under `releases/v3/` and activates `current/v3`. Update the Android manifest URL in `android/build.gradle.kts` to use `/current/v3/` in the same change.

The upload response contains the release timestamp. To reactivate a previous ABI release, use that timestamp with the rollback endpoint:

```sh
curl --fail --request POST https://claw.msfjarvis.dev/admin/rollback \
  --header "Authorization: Bearer $ADMIN_PASSWORD" \
  --header "Content-Type: application/json" \
  --header "X-Zipline-Version: v2" \
  --data '{"releaseTimestamp":"20260830T000000Z"}'
```
