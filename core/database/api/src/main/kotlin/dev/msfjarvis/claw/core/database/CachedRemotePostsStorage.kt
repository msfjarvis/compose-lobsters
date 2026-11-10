/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import dev.msfjarvis.claw.model.UIPost

/** Persistence operations for the short-lived remote feed cache. */
interface CachedRemotePostsStorage {
  suspend fun getCachedPosts(): List<UIPost>

  suspend fun getRecentPosts(limit: Long): List<UIPost>

  suspend fun replaceCachedPosts(posts: List<UIPost>)
}
