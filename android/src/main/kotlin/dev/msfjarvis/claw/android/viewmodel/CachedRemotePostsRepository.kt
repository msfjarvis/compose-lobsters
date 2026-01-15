/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.CachedRemotePost
import dev.msfjarvis.claw.database.local.CachedRemotePostQueries
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
class CachedRemotePostsRepository(
  private val cachedRemotePostQueries: CachedRemotePostQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  val cachedPosts = cachedRemotePostQueries.selectAllPosts().asFlow().mapToList(readDispatcher)

  fun getRecentPosts(limit: Long) =
    cachedRemotePostQueries.selectRecentPosts(limit).asFlow().mapToList(readDispatcher)

  suspend fun getCachedPosts(): List<CachedRemotePost> {
    return withContext(readDispatcher) { cachedRemotePostQueries.selectAllPosts().executeAsList() }
  }

  suspend fun savePosts(posts: List<CachedRemotePost>) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "Caching remote posts: ${posts.joinToString(",") { it.shortId }}")
    }
    withContext(writeDispatcher) {
      cachedRemotePostQueries.transaction {
        cachedRemotePostQueries.deleteAllPosts()
        posts.forEach { post -> cachedRemotePostQueries.insertOrReplacePost(post) }
      }
    }
  }

  private companion object {
    private const val TAG = "CachedRemotePostsRepository"
  }
}
