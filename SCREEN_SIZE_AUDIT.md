# Adaptive screen-size audit

**Scope:** Static, read-only review of `android/` and the feature modules that supply its screens. No files were changed and no build, emulator, or screenshot tests were run. Findings below describe code behavior and risks to verify, not observed device failures.

## Existing support

- The app uses Compose and Navigation 3. `MainActivity` calculates a window size class, and `ClawNavigationType` selects a bottom bar for compact width and a rail for medium/expanded width.
- Feed-to-comments list-detail support already exists: `LobstersPostsScreen.kt:139` creates a `ListDetailSceneStrategy`; the feed entries declare list panes, and comments declares a detail pane.
- The feed, search, comments, profile, settings, and reply screens generally use saveable scroll-state factories. Search UI state and the Navigation 3 back stack also have explicit saveable state.

## Findings and recommended changes

### 1. Configuration changes are suppressed instead of exercising state restoration — high

**Code:** `android/src/main/AndroidManifest.xml:30` declares an unusually broad `android:configChanges`, including orientation, screen size, density, font scale, and locale. The adaptive skill explicitly advises against suppressing Activity recreation for these changes.

**Impact:** Rotation, folding, resizing, and other changes do not exercise the normal recreation path. Removing the declaration without testing can expose state-loss and navigation regressions.

**Change:** Remove the opt-out after adding recreation tests. In particular:

- `feature/tags/impl/src/main/kotlin/dev/msfjarvis/claw/feature/tags/impl/TagList.kt:70–72` keeps date-picker selection and discard-dialog visibility in plain `remember`; decide which interaction state should survive recreation and save it together.
- `feature/auth/impl/src/main/kotlin/dev/msfjarvis/claw/android/ui/screens/LoginScreen.kt:52–54, 76–130` creates a WebView that initially loads the login URL. Test recreation during login and preserve appropriate WebView/navigation state; do not attempt to save the WebView object itself.
- `android/src/main/kotlin/dev/msfjarvis/claw/android/MainActivity.kt:55–58, 98–105` handles the launch intent on every `onCreate`. Test recreation from a deep link so restoring the back stack does not inadvertently replay navigation.
- `feature/discussion/impl/src/main/kotlin/dev/msfjarvis/claw/common/comments/reply/ReplyViewModel.kt:34` holds the reply draft only in a ViewModel. It survives ordinary Activity recreation, but not process death; save the draft if process-death restoration is required.

**References:** [Handle configuration changes](https://developer.android.com/guide/topics/resources/runtime-changes), [Save UI state in Compose](https://developer.android.com/develop/ui/compose/state-saving).

### 2. Navigation chrome is hidden even when the list remains visible — high

**Code:** `android/src/main/kotlin/dev/msfjarvis/claw/android/ui/screens/LobstersPostsScreen.kt:374–393` hides both the bottom bar and rail whenever the current back-stack destination is not top-level. On an expanded list-detail scene, selecting comments makes the current destination `Comments` even though the feed pane can remain visible. `android/src/main/kotlin/dev/msfjarvis/claw/android/ui/decorations/ClawAppBar.kt:66–79` also displays a global back arrow for every non-top-level destination.

**Impact:** The expanded layout can lose its top-level navigation rail while simultaneously showing the list, and presents a phone-style back affordance for a visible detail pane.

**Change:** Derive chrome visibility and the back affordance from the **active scene/pane presentation**, not just `backStack.lastOrNull()`. Keep top-level navigation accessible in a list-detail scene and suppress the detail back arrow when both panes are visible; retain compact-screen back behavior. Consider `NavigationSuiteScaffold` as recommended by the adaptive skill, preserving current selection, reselection-to-scroll-to-top, and search visibility behavior.

**References:** [Build an adaptive navigation UI](https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation), [Navigation 3 adaptive layouts](https://developer.android.com/guide/navigation/navigation-3/adaptive).

### 3. Search uses a full-screen presentation at every width — medium

**Code:** `android/src/main/kotlin/dev/msfjarvis/claw/android/ui/decorations/ClawAppBar.kt:81–135` always uses `ExpandedFullScreenContainedSearchBar`; `LobstersPostsScreen.kt:374–393` hides navigation while search is active. Search results reuse the single-column feed list.

**Impact:** A tablet or desktop-sized window does not gain a wider-screen search presentation or retain navigation context. The actual presentation should be checked at medium and expanded widths, including keyboard and IME resizing.

**Change:** Choose a window-aware search presentation, such as a bounded search surface on larger windows, while preserving compact full-screen behavior. Test query, results, dismissal, focus, and navigation across resizing. Also verify state synchronization: `ClawAppBar.kt:82` creates a separate `rememberTextFieldState()` while the supplied `mode.query` is used by the clear button; restoration and visible input should agree.

**References:** [Build adaptive apps](https://developer.android.com/develop/ui/compose/layouts/adaptive), [Save UI state in Compose](https://developer.android.com/develop/ui/compose/state-saving).

### 4. Several content surfaces do not use available width deliberately — medium

**Code:**

- `feature/posts/impl/src/main/kotlin/dev/msfjarvis/claw/android/ui/lists/NetworkPosts.kt:84` and `DatabasePosts.kt:60` use single-column lists; search also uses `NetworkPosts`.
- `feature/settings/impl/src/main/kotlin/dev/msfjarvis/claw/android/ui/screens/SettingsScreen.kt:89`, `feature/profile/impl/src/main/kotlin/dev/msfjarvis/claw/common/user/UserProfile.kt:85–93`, and `feature/discussion/impl/src/main/kotlin/dev/msfjarvis/claw/common/comments/reply/ReplyScreen.kt:91–122` use vertically arranged content without an explicit wide-screen content-width policy.

**Impact:** When these destinations or panes have substantial width, rows and text fields can become excessively wide, and feeds may leave an opportunity to show more content. The existing list-detail scene reduces available width, so this should be evaluated **per pane**, not solely by whole-window width.

**Change:** Evaluate bounded, centered reading widths or a pane-aware adaptive grid for feeds and saved/search results. Preserve paging, stable item behavior, saved-post section headings, and scroll restoration if changing list type. Keep threaded comments as a readable ordered column unless testing supports another presentation. Constrain settings, profile, and reply content where appropriate rather than mechanically converting every list to a grid.

**References:** [Support different screen sizes](https://developer.android.com/develop/ui/compose/layouts/adaptive/support-different-screen-sizes), [Lazy grids](https://developer.android.com/develop/ui/compose/lists#lazy-grids).

### 5. Full-screen, multi-form-factor regression coverage is missing — high

**Code:** `android/src/screenshotTest/kotlin/dev/msfjarvis/claw/android/ui/decorations/ClawAppBarPreviewScreenshotTest.kt:16–75` tests app-bar states but does not specify form factors or exercise the full navigation scene. There are additional component and reply screenshot tests, but the audit found no full app-screen screenshot tests covering tablet, foldable, and desktop sizes.

**Change:** Add deterministic screen-level screenshot coverage for compact, medium, expanded, and short-height windows: feed with no selection, feed plus comments, search, settings, and reply with IME-relevant constraints. Add interaction/recreation tests for navigation, selection, scroll position, search, draft state, and resizing across breakpoints. Include large font scale and a fold/unfold or equivalent resize test. Keep baseline updates for human visual review.

**References:** [Compose Preview Screenshot Testing](https://developer.android.com/studio/preview/compose-screenshot-testing), [Test different screen and window sizes](https://developer.android.com/training/testing/different-screens).

## Suggested implementation order

1. Add baseline screen and recreation coverage.
2. Fix scene-aware navigation chrome and back affordances.
3. Remove the configuration-change opt-out and address the restoration cases revealed by tests.
4. Adapt search and establish pane-aware content widths; evaluate grids using actual layouts.

The skill’s tabletop-posture and media-playback steps do not appear to be primary matches for the reviewed app screens. Desktop-specific shortcuts and tooltips were outside this screen-size-focused audit.