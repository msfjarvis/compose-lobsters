/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.discussion.impl.navigation

import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriMatchResult
import dev.msfjarvis.claw.android.ui.navigation.Comments

/** Matches discussion links and derives the canonical URL required by the destination. */
fun discussionDeepLinkMatcher(scheme: String): UriDeepLinkMatcher<Comments> =
  object :
    UriDeepLinkMatcher<Comments>(
      DeepLinkUri("$scheme://comments/{postId}"),
      Comments.serializer(),
    ) {
    override fun matchArguments(
      pathArgs: Map<String, List<String>>,
      queryArgs: Map<String, List<String>>,
      fragmentArgs: Map<String, List<String>>,
    ): UriMatchResult<Comments>? {
      val postId = pathArgs["postId"]?.singleOrNull()?.takeIf { it.isNotBlank() } ?: return null
      return super.matchArguments(
        pathArgs + ("commentsUrl" to listOf("https://lobste.rs/s/$postId/c")),
        queryArgs,
        fragmentArgs,
      )
    }
  }
