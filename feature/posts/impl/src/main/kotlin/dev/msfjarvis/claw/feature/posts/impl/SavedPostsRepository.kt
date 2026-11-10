/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import android.util.Log
import dev.msfjarvis.claw.core.database.SavedPostsStorage
import dev.msfjarvis.claw.feature.posts.api.SavedPostReference
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@Inject
@ContributesBinding(AppScope::class, binding = binding<SavedPosts>())
class SavedPostsRepository(private val storage: SavedPostsStorage) : SavedPosts {
  val savedPosts = storage.posts
  override val savedPostsSortedByDate = storage.postsSortedByDate
  override val savedPostsCount = storage.count
  override val savedPostIds = storage.postIds

  suspend fun getSavedPosts() = storage.getPosts()

  override suspend fun getPostIdsFromLastNDays(days: Long): List<SavedPostReference> =
    storage.getPostIdsFromLastNDays(days).map { SavedPostReference(it.shortId, it.commentsUrl) }

  override suspend fun getRecentPosts(limit: Long) = storage.getRecentPosts(limit)

  override suspend fun toggleSave(post: UIPost) {
    val saved = storage.togglePost(post)
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "${if (saved) "Saving" else "Removing"} post: ${post.shortId}")
    }
  }

  override suspend fun savePosts(posts: List<UIPost>) {
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "Saving posts: ${posts.joinToString(",") { it.shortId }}")
    }
    storage.savePosts(posts)
  }

  override suspend fun importSerializedPosts(serialized: String) =
    storage.importSerializedPosts(serialized)

  override suspend fun exportSerializedPosts() = storage.exportSerializedPosts()

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
