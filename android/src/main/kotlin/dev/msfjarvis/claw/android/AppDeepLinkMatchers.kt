/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.navigation.api.DeepLinkMatcher
import java.net.URI
import java.net.URISyntaxException

/** Assemble the route matchers contributed by the app's current feature implementations. */
internal fun appDeepLinkMatchers(scheme: String): List<DeepLinkMatcher> =
  listOf(PostsDeepLinkMatcher(scheme), DiscussionDeepLinkMatcher(scheme))

private class PostsDeepLinkMatcher(private val scheme: String) : DeepLinkMatcher {
  override fun match(uri: String): NavKey? {
    val data = uri.toUriOrNull() ?: return null
    if (data.scheme != scheme) return null
    return when (data.host) {
      "newest" -> Newest
      "hottest" -> Hottest
      "saved" -> Saved
      else -> null
    }
  }
}

private class DiscussionDeepLinkMatcher(private val scheme: String) : DeepLinkMatcher {
  override fun match(uri: String): NavKey? {
    val data = uri.toUriOrNull() ?: return null
    if (data.scheme != scheme || data.host != "comments") return null
    return data.path.orEmpty().split('/').firstOrNull(String::isNotBlank)?.let { postId ->
      // The short id is sufficient; synthesize the canonical comments URL.
      Comments(postId, "https://lobste.rs/s/$postId/c")
    }
  }
}

internal fun String.toUriOrNull(): URI? =
  try {
    URI(this)
  } catch (_: URISyntaxException) {
    null
  }
