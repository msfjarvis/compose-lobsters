/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import dev.msfjarvis.claw.feature.discussion.impl.navigation.discussionDeepLinkMatcher
import dev.msfjarvis.claw.feature.posts.impl.navigation.postsDeepLinkMatcher
import dev.msfjarvis.claw.navigation.api.DeepLinkMatcher

/** Assemble the route matchers contributed by the app's current feature implementations. */
internal fun appDeepLinkMatchers(scheme: String): List<DeepLinkMatcher> =
  listOf(postsDeepLinkMatcher(scheme), discussionDeepLinkMatcher(scheme))
