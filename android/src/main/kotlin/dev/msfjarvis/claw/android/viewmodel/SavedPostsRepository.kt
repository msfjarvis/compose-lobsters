/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import android.util.Log
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.core.database.SavedPostsStorage
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.Inject

@Inject
class SavedPostsRepository(private val storage: SavedPostsStorage) {
  val savedPosts = storage.posts
  val savedPostsSortedByDate = storage.postsSortedByDate
  val savedPostsCount = storage.count
  val savedPostIds = storage.postIds

  suspend fun getSavedPosts() = storage.getPosts()

  suspend fun getPostIdsFromLastNDays(days: Long) = storage.getPostIdsFromLastNDays(days)

  suspend fun getRecentPosts(limit: Long) = storage.getRecentPosts(limit)

  suspend fun toggleSave(post: UIPost) {
    val saved = storage.togglePost(post)
    if (BuildConfig.ENABLE_LOGS) {
      Log.d(TAG, "${if (saved) "Saving" else "Removing"} post: ${post.shortId}")
    }
  }

  suspend fun savePosts(posts: List<UIPost>) {
    if (BuildConfig.ENABLE_LOGS) {
      Log.d(TAG, "Saving posts: ${posts.joinToString(",") { it.shortId }}")
    }
    storage.savePosts(posts)
  }

  suspend fun importSerializedPosts(serialized: String) = storage.importSerializedPosts(serialized)

  suspend fun exportSerializedPosts() = storage.exportSerializedPosts()

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
