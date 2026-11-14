# Navigation 3: shared bounds do not follow predictive back in 1.3.0-alpha04

## Summary

When navigating from a post list to its comments, the list card and comments header use `Modifier.sharedBounds` with the same post ID. During a predictive-back gesture, the two destinations slide with the gesture, but the shared header can remain overlaid near its original position rather than moving towards the list card. This obscures part of the returning list and leaves the card's location apparently empty.

The app used Navigation 3 **1.3.0-alpha04** when the issue was reported. Pinning Navigation 3 to **1.2.0** is a workaround: in a debug build on the same device, the shared content visibly moved towards the card during the gesture. This is a regression report, not a confirmed diagnosis of the AndroidX implementation.

## Environment

- App: [compose-lobsters](https://github.com/msfjarvis/compose-lobsters), Android app, single-pane list-to-detail navigation.
- Device: Pixel 4a (`sunfish`), Android 17, API 37; 1080 × 2340 display.
- App compile SDK: 37.2; target SDK: 37.
- Compose animation, foundation, runtime and UI: 1.13.0-rc01; Material 3: 1.5.0-rc01; Material 3 adaptive/navigation3: 1.4.0-alpha03.
- Navigation 3 runtime and UI: 1.3.0-alpha04 reported failing; 1.2.0 tested as workaround.

## Steps to reproduce

1. Build the app at commit `1941fc6`, which contains the shared-bounds implementation and sets Navigation 3 to 1.3.0-alpha04. Launch the FOSS debug variant on a gesture-navigation device with predictive back enabled.
2. Open **Hottest** or **Newest**, then tap a post's comments button. Wait for the comments screen and header to appear.
3. Slowly swipe back from the right edge and hold the gesture partway through. Observe the list, comments screen, and shared header before releasing.
4. For comparison, build commit `3438f8a`, which changes the Navigation 3 version to 1.2.0 while retaining the same shared-bounds implementation, and repeat. Feed content and exact finger position can differ.

Expected: shared bounds move between the comments header and the selected post card in step with predictive-back progress, including when the gesture is cancelled.

Observed in the reported failing build: the screens move but the shared header remains drawn over the top of the returning list. The effect was also observed with an automated slow right-edge swipe (`adb shell input touchscreen swipe 1075 1120 650 1120 6000`), capturing the screen while the swipe was in progress.

Observed with 1.2.0: the shared content moved towards the list card during a slow back swipe; returning the gesture to the edge restored the comments screen. This is a visual observation, not an automated pixel assertion.

## Screenshots

- [Reported failure, gesture held](images/navigation3-predictive-back/reported-failure.png): the header is overlaid above the moving screens. This was captured from the originally installed app; its resolved dependency versions were **not independently verified from the APK**.
- [Navigation 3 1.2.0 debug build, gesture in progress](images/navigation3-predictive-back/navigation3-1.2.0.png): the shared content is partway between the header and card. This is a different post and gesture position; it is not a pixel-matched A/B comparison.

The 1.2.0 build was installed under a temporary application ID to avoid replacing the existing app. The isolated 1.3.0-alpha04 build compiled and installed, but the device locked before its gesture could be captured. A controlled same-post, same-progress comparison and a minimal standalone sample would strengthen this report.

## Relevant integration

The actual implementation is in the repository at the commits above:

- `core/navigation/impl/src/main/kotlin/dev/msfjarvis/claw/navigation/impl/NavigationHost.kt`: wraps `NavDisplay` in `SharedTransitionLayout`.
- `core/ui/src/main/kotlin/dev/msfjarvis/claw/common/ui/PostSharedTransition.kt`: calls `sharedBounds`, using `LocalNavAnimatedContentScope.current` and a `post-$postId` key.
- `feature/posts/impl/src/main/kotlin/dev/msfjarvis/claw/android/ui/lists/LobstersListItem.kt` and `feature/discussion/impl/src/main/kotlin/dev/msfjarvis/claw/common/comments/CommentsPageImpl.kt`: apply the matching modifier to card and header.
- `android/src/main/kotlin/dev/msfjarvis/claw/android/ui/screens/LobstersPostsScreen.kt`: supplies horizontal slide transitions for both regular and predictive pops and uses `ListDetailSceneStrategy`.

This follows the documented pattern of a `SharedTransitionLayout` around `NavDisplay`, with `LocalNavAnimatedContentScope` supplied to shared elements or bounds: [Navigation with shared elements](https://developer.android.com/develop/ui/compose/animation/shared-elements/navigation).

## Potential investigation lead

The [1.2.0 `NavDisplay` source](https://android.googlesource.com/platform/frameworks/support/+/811e8c9c37f4955d520fe0cf7e39d96866066abc/navigation3/navigation3-ui/src/commonMain/kotlin/androidx/navigation3/ui/NavDisplay.kt) uses `SeekableTransitionState` and seeks it to back-gesture progress. The [1.3.0-alpha04 source](https://android.googlesource.com/platform/frameworks/support/+/a021adf44194ae86e596ae25ca43d5e4e18f6926/navigation3/navigation3-ui/src/commonMain/kotlin/androidx/navigation3/ui/NavDisplay.kt) uses `DeferredTransitionState` and supplies progress through a mutable content transform. That implementation change is a possible area to investigate, **not a demonstrated root cause**. The [Navigation 3 release notes](https://developer.android.com/jetpack/androidx/releases/navigation3#1.3.0-alpha04) describe the transition change and other fixes but do not establish this exact issue as known.
