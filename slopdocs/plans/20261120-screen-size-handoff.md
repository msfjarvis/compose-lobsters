# Screen-size audit handoff

## Workspace and priority

- Continue in `.worktrees/screen-size-adaptation` on branch `hshandilya.2026-11-20-screen-size-adaptation`. The tree was clean at handoff; work is committed in focused commits after `main`.
- The user's audit is **untracked in the original checkout** at `../../SCREEN_SIZE_AUDIT.md` relative to this worktree; leave it untouched. It is not part of the branch.
- The user requests meaningful parallel delegation for independent work, with one Gradle execution owner.
- **After the remaining audit and verification work, investigate and fix the post-a-day reminder toggle last.** User reports clicking it does not update the displayed state until leaving and reopening settings. It has not been diagnosed or fixed. Reproduce and test both enable/disable, permission-granted and permission-denied paths before changing it.

## Implemented

- Navigation 3 scene-aware chrome, compact back arrow, a close-detail control on wider scenes, and `PopLatest` back behavior. The selected top-level feed remains in the rail; wider search uses a docked popup. Actual scene membership and pane visibility are distinct: `ThreePaneScaffoldScene.entries` includes hidden entries, so `LobstersPostsScreen.kt` calculates pane visibility from the strategy directive and destination history.
- Removed the `MainActivity` configuration-change opt-out. Deep links are not replayed after restoration; tag dialog state, login WebView navigation history, reply draft and selection, search input, and search scroll are saved. WebView history restoration does **not** guarantee unsent form input survives recreation; do not serialize passwords into saved state.
- Search process restoration starts the pager at the page implied by the saved list index, with existing `itemsBefore` placeholders; new searches start at page one. The API provides page numbers, not stable absolute item IDs or a total count, so results changing between save and restore and short intermediate pages remain important edge cases. `SearchResultsList` avoids measuring an empty lazy list during refresh, preventing premature anchor clamping.
- Feed, saved-post, comments, settings, profile and reply content use centered reading widths. Feed/saved/comments lazy scroll viewports remain full-width, so swipes in gutters work. Settings import/export controls wrap below their label for large fonts.
- Login Retry clears restored failed history. The reminder time-picker retains its visible state and draft across recreation and does not commit on cancel.
- Screenshot comparison tolerance is shared by `dev.msfjarvis.claw.android-screenshot` (0.04%) for host-dependent rendering differences. The app applies the standalone screenshot plugin directly, so this convention does not apply to the app.

## Verification completed

- Repository CI-equivalent command passed after the current changes: `./gradlew --no-configuration-cache :build-logic:check check validateDebugScreenshotTest -PslimTests`. Run Gradle through the `gradle-run` skill wrapper.
- Host screenshot baselines exist for feed/saved posts, comments, settings and reply at several compact, medium, expanded, short-height and large-font sizes. Some were visually inspected; **human visual approval is still needed**. Full-app deterministic screenshots covering the actual feed-plus-comments scene and expanded search are still missing. The Compose preview renderer did not capture the expanded docked-search popup, so a misleading empty-popup baseline was removed.
- Tablet emulator: targeted instrumentation passed for expanded navigation/close, search recreation, deep-link recreation, login WebView history/retry, process-style paged search restoration, search scroll reset, reminder picker restore/cancel, full-width feed/saved gutter scrolling, comments loading and reply draft restoration. Tests using `StateRestorationTester` emulate saved-state recreation, not a real OS process kill.
- A temporary tablet was also resized to compact width and back: the search query survived; compact comments/back and tablet navigation tests passed. Medium-width comments and 1.5x font were inspected manually. Test outcomes depending on an external login page are not proof of a complete sign-in flow.
- The configured Gradle managed emulator initially failed on NixOS because the SDK emulator could not find `libX11.so.6`. A temporary Pixel Tablet AVD worked with `ANDROID_AVD_HOME` in `/tmp` and `LD_LIBRARY_PATH="$(nix eval --raw nixpkgs#libx11)/lib"`; use `adb -s <emulator>` explicitly. The emulator started for this session has been stopped. A physical phone was connected with an existing debug app installed: ask before overwriting it.

## Remaining work

1. Add deterministic **full-app** screenshots for compact/medium/expanded/short-height feed, feed plus comments and search; exercise resize, navigation, scroll, selection and IME constraints. Existing screenshot tests cover individual screen surfaces, and activity instrumentation covers navigation, but neither substitutes for full-scene screenshot coverage. Retain baselines for human review.
2. Verify process death and scroll restoration with a real paged search, including a restored page that has disappeared or shortened; the committed fake paging test covers a full page at the saved anchor. Confirm login behavior during a real sign-in without capturing credentials. Consider focused saved-state tests for tag dialogs and reminder-picker text-entry mode.
3. Review the screenshots with the user and complete physical-portrait/IME checks once device use is authorized. Record any visual or interaction defects and fix them.
4. **Last:** diagnose and fix the user's post-a-day reminder toggle report, with regression tests. Then rerun repo checks and targeted device tests, commit focused changes, stop owned processes, and leave the tree clean.
