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
import dev.msfjarvis.claw.database.local.CachedHottestPost
import dev.msfjarvis.claw.database.local.CachedHottestPostQueries
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
class CachedHottestPostsRepository(
  private val cachedHottestPostQueries: CachedHottestPostQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  val cachedPosts = cachedHottestPostQueries.selectAllPosts().asFlow().mapToList(readDispatcher)

  fun getRecentPosts(limit: Long) =
    cachedHottestPostQueries.selectRecentPosts(limit).asFlow().mapToList(readDispatcher)

  suspend fun getCachedPosts(): List<CachedHottestPost> {
    return withContext(readDispatcher) { cachedHottestPostQueries.selectAllPosts().executeAsList() }
  }

  suspend fun savePosts(posts: List<CachedHottestPost>) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "Caching hottest posts: ${posts.joinToString(",") { it.shortId }}")
    }
    withContext(writeDispatcher) {
      cachedHottestPostQueries.transaction {
        cachedHottestPostQueries.deleteAllPosts()
        posts.forEach { post -> cachedHottestPostQueries.insertOrReplacePost(post) }
      }
    }
  }

  private companion object {
    private const val TAG = "CachedHottestPostsRepository"
  }
}
