/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.database.CachedRemotePostsStorage
import dev.msfjarvis.claw.database.local.CachedRemotePost
import dev.msfjarvis.claw.database.local.CachedRemotePostQueries
import dev.msfjarvis.claw.database.local.fromCachedRemotePost
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightCachedRemotePostsStorage(
  private val queries: CachedRemotePostQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : CachedRemotePostsStorage {
  override suspend fun getCachedPosts(): List<UIPost> =
    withContext(readDispatcher) {
      queries.selectAllPosts().executeAsList().map(UIPost.Companion::fromCachedRemotePost)
    }

  override suspend fun getRecentPosts(limit: Long): List<UIPost> =
    withContext(readDispatcher) {
      queries.selectRecentPosts(limit).executeAsList().map(UIPost.Companion::fromCachedRemotePost)
    }

  override suspend fun replaceCachedPosts(posts: List<UIPost>) =
    withContext(writeDispatcher) {
      queries.transaction {
        queries.deleteAllPosts()
        posts.forEachIndexed { index, post ->
          queries.insertOrReplacePost(post.toCachedRemotePost(insertionOrder = index))
        }
      }
    }
}

internal fun UIPost.toCachedRemotePost(insertionOrder: Int) =
  CachedRemotePost(
    shortId = shortId,
    title = title,
    url = url,
    createdAt = createdAt,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = submitter,
    tags = tags,
    description = description,
    userIsAuthor = userIsAuthor,
    insertionOrder = insertionOrder,
  )
