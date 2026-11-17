/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl.navigation

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved

/** Matches the external routes owned by the posts feature. */
fun postsDeepLinkMatchers(scheme: String): List<DeepLinkMatcher<NavKey, *>> =
  listOf(
    UriDeepLinkMatcher(DeepLinkUri("$scheme://newest"), Newest.serializer()),
    UriDeepLinkMatcher(DeepLinkUri("$scheme://hottest"), Hottest.serializer()),
    UriDeepLinkMatcher(DeepLinkUri("$scheme://saved"), Saved.serializer()),
  )
