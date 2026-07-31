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
import app.cash.sqldelight.coroutines.mapToOne
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.database.local.SavedPostQueries
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toSavedPost
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
class SavedPostsRepository(
  private val savedPostQueries: SavedPostQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  val savedPosts = savedPostQueries.selectAllPosts().asFlow().mapToList(readDispatcher)
  val savedPostsSortedByDate =
    savedPostQueries.selectAllPostsSortedByDate().asFlow().mapToList(readDispatcher)
  val savedPostsCount = savedPostQueries.selectCount().asFlow().mapToOne(readDispatcher)
  val savedPostIds = savedPostQueries.selectPostIds().asFlow().mapToList(readDispatcher)

  suspend fun getSavedPosts() =
    withContext(readDispatcher) { savedPostQueries.selectAllPosts().executeAsList() }

  suspend fun getPostIdsFromLastNDays(days: Long) =
    withContext(readDispatcher) {
      savedPostQueries.selectPostIdsFromLastNDays(days.toString()).executeAsList()
    }

  suspend fun getRecentPosts(limit: Long) =
    withContext(readDispatcher) { savedPostQueries.selectRecentPosts(limit).executeAsList() }

  suspend fun toggleSave(post: UIPost) {
    withContext(writeDispatcher) {
      savedPostQueries.transaction {
        val removed = savedPostQueries.deletePost(post.shortId).executeAsOneOrNull() != null
        if (removed) {
          if (BuildConfig.DEBUG) Log.d(TAG, "Removing post: ${post.shortId}")
        } else {
          if (BuildConfig.DEBUG) Log.d(TAG, "Saving post: ${post.shortId}")
          savedPostQueries.insertOrReplacePost(post.toSavedPost())
        }
      }
    }
  }

  suspend fun savePosts(posts: List<SavedPost>) {
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "Saving posts: ${posts.joinToString(",") { it.shortId }}")
    }
    withContext(writeDispatcher) {
      savedPostQueries.transaction {
        posts.forEach { post -> savedPostQueries.insertOrReplacePost(post) }
      }
    }
  }

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
