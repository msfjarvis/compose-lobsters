# Screenshot test suite cannot load generated resource class

## State and reproduction

Unresolved. Migration from the standalone Compose screenshot plugin to AGP test suites is incomplete on worktree `.worktrees/screenshot-suites`, branch `hshandilya.2026-11-23-screenshot-suites`, commit `b04c378`. AGP 9.5.2; screenshot engine 0.0.1-alpha16. The pre-migration baseline `:core:ui:validateDebugScreenshotTest` passed.

The migrated suite is named `previewTest` and targets `debug` for libraries. Run `:core:ui:testDebugPreviewTestDefaultTestSuite` using the gradle-run skill wrapper. It discovers and executes screenshot tests, but rendering fails in `ComposeViewAdapter`:

```
java.lang.ClassNotFoundException: androidx.customview.poolingcontainer.R$id
  at androidx.customview.poolingcontainer.PoolingContainer.<clinit>(PoolingContainer.kt:121)
```

See `core/ui/build/intermediates/debug/testDebugPreviewTestDefaultTestSuite/results/TEST-dev.msfjarvis.claw.common.posts.LobstersCardPreviewScreenshotTestKt.xml` after running. This is a render failure, before a meaningful golden comparison.

## Evidence and experiments

- `core/ui/build/intermediates/compile_and_runtime_r_class_jar/debugPreviewTest/processDebugPreviewTestResources/R.jar` contains `androidx/customview/poolingcontainer/R$id.class`. Generation succeeded; whether and why the renderer cannot see that JAR remains unproven. Inspect actual renderer inputs/classloader before claiming root cause.
- `-Pandroid.enableAppCompileTimeRClass=false` did not fix the failure.
- Adding `implementation("androidx.customview:customview-poolingcontainer:1.1.0")` to `core/ui` did not fix the failure. The experimental dependency was removed.
- Setting `testOptions.unitTests.isIncludeAndroidResources = true` in the library convention plugin did not fix the failure. This experiment was removed.
- The legacy plugin's suite name `screenshotTest` collided with an existing `assembleFossDebugScreenshotTest` task when registered as an AGP suite. Renaming the suite to `previewTest` avoided that configuration failure. Check whether the legacy flag or other configuration caused the collision before treating renaming as required.
- The generic documentation's task ordering did not match this build: AGP exposed `testDebugPreviewTestDefaultTestSuite` for libraries and `testFossDebugPreviewTestDefaultTestSuite` for the app. Update tasks use `update` in place of `test`.

## External leads

- [Google issue 381531634](https://issuetracker.google.com/issues/381531634) discusses a different missing class in screenshot tests and recommends adding dependencies as `implementation`. Do not infer it proves the same cause here. The tracker page was not independently readable through our fetch tool; the user confirmed the advice.
- [Older Compose Preview report](https://stackoverflow.com/questions/72807725/noclassdeffounderror-could-not-initialize-class-androidx-customview-poolingcont/72807850) fixed a `PoolingContainer` initialization problem with a debug dependency. It predates test suites and is not evidence of the same generated-R-class failure.
- [AGP screenshot test suite documentation](https://developer.android.com/studio/preview/compose-screenshot-testing-with-testsuites) describes the experimental suite configuration; validate task names and reference paths against actual AGP output rather than assuming the examples match this version.

## Remaining migration work

- Determine why the suite renderer cannot load the generated resource class; reproduce a passing core UI test and compare with the old plugin's inputs. Check actual renderer classpath / generated resource JAR wiring; do not assume absence from the classpath solely from the exception.
- Verify and correct golden reference locations for each variant without overwriting existing baselines blindly. The partial commit moved library references to `src/previewTestDefaultDebug` and app foss references to `src/previewTestDefaultFossDebug`; these paths have not been validated. The pre-existing `android/src/screenshotTestDebug` references were not migrated.
- Migrate and verify feature-specific screenshot dependencies (`feature/posts/impl` and `feature/discussion/impl` still declare `screenshotTestImplementation(libs.coil3.compose)`). Verify all suites and app variants, including whether gplay coverage was previously intended.
- Update `.github/workflows/ci.yml`, which still invokes `validateDebugScreenshotTest`. Remove obsolete standalone plugin catalog entries if unused. Preserve or deliberately replace the old 0.0004 pixel-difference threshold configured by the library convention plugin; the partial migration removed it.
- Validate the actual suite tasks against all existing goldens and run repo formatting/checks. Do not treat task registration, compilation, or an empty suite as a passing screenshot migration.
