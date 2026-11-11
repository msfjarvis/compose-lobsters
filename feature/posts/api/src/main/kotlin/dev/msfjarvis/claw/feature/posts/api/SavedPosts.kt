/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.api

import dev.msfjarvis.claw.model.UIPost
import kotlinx.coroutines.flow.Flow

/** Saved-post operations shared by the application, workers, and widgets. */
interface SavedPosts {
  val savedPostsSortedByDate: Flow<List<UIPost>>
  val savedPostsCount: Flow<Long>
  val savedPostIds: Flow<List<String>>

  suspend fun getPostIdsFromLastNDays(days: Long): List<SavedPostReference>

  suspend fun getRecentPosts(limit: Long): List<UIPost>

  suspend fun toggleSave(post: UIPost)

  suspend fun savePosts(posts: List<UIPost>)

  suspend fun importSerializedPosts(serialized: String)

  suspend fun exportSerializedPosts(): String
}

/** The saved-post fields needed when refreshing posts from the network. */
data class SavedPostReference(val shortId: String, val commentsUrl: String)
