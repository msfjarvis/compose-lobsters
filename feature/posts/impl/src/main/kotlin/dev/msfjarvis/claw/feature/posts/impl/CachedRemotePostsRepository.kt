/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import android.util.Log
import dev.msfjarvis.claw.core.database.CachedRemotePostsStorage
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.Inject

@Inject
class CachedRemotePostsRepository(private val storage: CachedRemotePostsStorage) {
  suspend fun getCachedPosts(): List<UIPost> = storage.getCachedPosts()

  suspend fun getRecentPosts(limit: Long): List<UIPost> = storage.getRecentPosts(limit)

  suspend fun savePosts(posts: List<UIPost>) {
    if (Log.isLoggable(TAG, Log.DEBUG)) {
      Log.d(TAG, "Caching remote posts: ${posts.joinToString(",") { it.shortId }}")
    }
    storage.replaceCachedPosts(posts)
  }

  private companion object {
    private const val TAG = "CachedRemotePosts"
  }
}
