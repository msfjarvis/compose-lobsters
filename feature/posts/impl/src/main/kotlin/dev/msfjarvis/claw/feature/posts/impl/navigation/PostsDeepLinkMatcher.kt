/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl.navigation

import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.navigation.api.DeepLinkMatcher
import java.net.URI
import java.net.URISyntaxException

/** Matches the external routes owned by the posts feature. */
fun postsDeepLinkMatcher(scheme: String): DeepLinkMatcher = DeepLinkMatcher { uri ->
  val data = uri.toUriOrNull() ?: return@DeepLinkMatcher null
  if (data.scheme != scheme) return@DeepLinkMatcher null
  when (data.host) {
    "newest" -> Newest
    "hottest" -> Hottest
    "saved" -> Saved
    else -> null
  }
}

private fun String.toUriOrNull(): URI? =
  try {
    URI(this)
  } catch (_: URISyntaxException) {
    null
  }
