/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.api

import dev.msfjarvis.claw.model.UIPost

/** Provides the latest hottest posts, falling back to cached feed data when offline. */
fun interface HottestPostsSource {
  suspend fun getHottestPosts(): List<UIPost>
}
