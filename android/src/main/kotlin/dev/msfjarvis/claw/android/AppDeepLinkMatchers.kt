/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import dev.msfjarvis.claw.feature.discussion.impl.navigation.discussionDeepLinkMatcher
import dev.msfjarvis.claw.feature.posts.impl.navigation.postsDeepLinkMatchers

/** Assemble the route matchers contributed by the app's current feature implementations. */
internal fun appDeepLinkMatchers(scheme: String): List<DeepLinkMatcher<NavKey, *>> =
  postsDeepLinkMatchers(scheme) + discussionDeepLinkMatcher(scheme)

internal fun matchDeepLink(
  request: DeepLinkRequest,
  matchers: List<DeepLinkMatcher<NavKey, *>>,
): NavKey? = matchers.mapNotNull { it.match(request) }.maxOrNull()?.key
