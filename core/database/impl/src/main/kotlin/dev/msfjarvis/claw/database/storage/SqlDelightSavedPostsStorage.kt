/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.coroutines.DefaultDispatcher
import dev.msfjarvis.claw.core.database.SavedPostIdAndCommentsUrl
import dev.msfjarvis.claw.core.database.SavedPostsStorage
import dev.msfjarvis.claw.database.SavedPostSerializer
import dev.msfjarvis.claw.database.local.SavedPostQueries
import dev.msfjarvis.claw.database.local.fromSavedPost
import dev.msfjarvis.claw.database.local.toSavedPost
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightSavedPostsStorage(
  private val queries: SavedPostQueries,
  private val json: Json,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
  @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : SavedPostsStorage {
  override val posts: Flow<List<UIPost>> =
    queries.selectAllPosts().asFlow().mapToList(readDispatcher).map { rows ->
      rows.map(UIPost.Companion::fromSavedPost)
    }

  override val postsSortedByDate: Flow<List<UIPost>> =
    queries.selectAllPostsSortedByDate().asFlow().mapToList(readDispatcher).map { rows ->
      rows.map(UIPost.Companion::fromSavedPost)
    }

  override val count = queries.selectCount().asFlow().mapToOne(readDispatcher)
  override val postIds = queries.selectPostIds().asFlow().mapToList(readDispatcher)

  override suspend fun getPosts(): List<UIPost> =
    withContext(readDispatcher) {
      queries.selectAllPosts().executeAsList().map(UIPost.Companion::fromSavedPost)
    }

  override suspend fun getPostIdsFromLastNDays(days: Long): List<SavedPostIdAndCommentsUrl> =
    withContext(readDispatcher) {
      queries.selectPostsFromLastNDays(days.toString()).executeAsList().map { row ->
        SavedPostIdAndCommentsUrl(shortId = row.shortId, commentsUrl = row.commentsUrl)
      }
    }

  override suspend fun getRecentPosts(limit: Long): List<UIPost> =
    withContext(readDispatcher) {
      queries.selectRecentPosts(limit).executeAsList().map(UIPost.Companion::fromSavedPost)
    }

  override suspend fun togglePost(post: UIPost): Boolean =
    withContext(writeDispatcher) {
      queries.transactionWithResult {
        val removed = queries.deletePost(post.shortId).executeAsOneOrNull() != null
        if (!removed) queries.insertOrReplacePost(post.toSavedPost())
        !removed
      }
    }

  override suspend fun savePosts(posts: List<UIPost>) =
    withContext(writeDispatcher) {
      queries.transaction {
        posts.forEach { post -> queries.insertOrReplacePost(post.toSavedPost()) }
      }
    }

  override suspend fun importSerializedPosts(serialized: String) {
    val posts =
      withContext(defaultDispatcher) {
        json.decodeFromString(ListSerializer(SavedPostSerializer), serialized)
      }
    withContext(writeDispatcher) {
      queries.transaction {
        posts.forEach(queries::insertOrReplacePost)
      }
    }
  }

  override suspend fun exportSerializedPosts(): String =
    withContext(readDispatcher) {
      json.encodeToString(
        ListSerializer(SavedPostSerializer),
        queries.selectAllPosts().executeAsList(),
      )
    }
}
