/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

// The navigation host and independently contributed destinations share one transition scope.
// A null default keeps standalone screens and previews usable outside the navigation host.
@Suppress("ComposeCompositionLocalUsage")
val LocalPostSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun Modifier.postSharedBounds(postId: String): Modifier {
  val scope = LocalPostSharedTransitionScope.current ?: return this
  val visibilityScope = LocalNavAnimatedContentScope.current
  return with(scope) {
    sharedBounds(
      sharedContentState = rememberSharedContentState(key = "post-$postId"),
      animatedVisibilityScope = visibilityScope,
    )
  }
}
