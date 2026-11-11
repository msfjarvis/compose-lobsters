/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import dev.msfjarvis.claw.model.UIPost
import kotlinx.coroutines.flow.Flow

/** Persistence operations used by the saved-post repository. */
interface SavedPostsStorage {
  val posts: Flow<List<UIPost>>
  val postsSortedByDate: Flow<List<UIPost>>
  val count: Flow<Long>
  val postIds: Flow<List<String>>

  suspend fun getPosts(): List<UIPost>

  suspend fun getPostIdsFromLastNDays(days: Long): List<SavedPostIdAndCommentsUrl>

  suspend fun getRecentPosts(limit: Long): List<UIPost>

  /** Toggles the post's saved state and returns true when it was saved. */
  suspend fun togglePost(post: UIPost): Boolean

  suspend fun savePosts(posts: List<UIPost>)

  /** Imports the existing serialized database-row format without exposing SQL row types. */
  suspend fun importSerializedPosts(serialized: String)

  /** Exports posts in the existing serialized database-row format. */
  suspend fun exportSerializedPosts(): String
}

/** The saved-post fields required by reminder selection without exposing a SQLDelight row. */
data class SavedPostIdAndCommentsUrl(val shortId: String, val commentsUrl: String)
