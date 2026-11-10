/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.discussion.impl.navigation

import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.navigation.api.DeepLinkMatcher
import java.net.URI
import java.net.URISyntaxException

/** Matches discussion deep links without exposing discussion route keys to the app. */
fun discussionDeepLinkMatcher(scheme: String): DeepLinkMatcher = DeepLinkMatcher { uri ->
  val data = uri.toUriOrNull() ?: return@DeepLinkMatcher null
  if (data.scheme != scheme || data.host != "comments") return@DeepLinkMatcher null
  data.path.orEmpty().split('/').firstOrNull(String::isNotBlank)?.let { postId ->
    // The short id is sufficient; synthesize the canonical comments URL.
    Comments(postId, "https://lobste.rs/s/$postId/c")
  }
}

private fun String.toUriOrNull(): URI? =
  try {
    URI(this)
  } catch (_: URISyntaxException) {
    null
  }
