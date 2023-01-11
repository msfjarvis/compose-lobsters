/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.android.injection.DatabaseDispatcher
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.Comment
import io.github.aakira.napier.Napier
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SavedPostsRepository
@Inject
constructor(
  database: LobstersDatabase,
  @DatabaseDispatcher private val dbDispatcher: CoroutineDispatcher,
) {
  private val savedPostQueries = database.savedPostQueries
  private val postCommentsQueries = database.postCommentsQueries
  val savedPosts = savedPostQueries.selectAllPosts().asFlow().mapToList(dbDispatcher)

  suspend fun savePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Saving post: ${post.shortId}" }
    withContext(dbDispatcher) { savedPostQueries.insertOrReplacePost(post) }
  }

  suspend fun savePosts(posts: List<SavedPost>) {
    Napier.d(tag = TAG) { "Saving posts: ${posts.joinToString(",") { it.shortId }}" }
    withContext(dbDispatcher) {
      savedPostQueries.transaction {
        posts.forEach { post -> savedPostQueries.insertOrReplacePost(post) }
      }
    }
  }

  suspend fun removePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Removing post: ${post.shortId}" }
    withContext(dbDispatcher) { savedPostQueries.deletePost(post.shortId) }
  }

  suspend fun getSeenComments(postId: String) =
    withContext(dbDispatcher) { postCommentsQueries.getCommentIds(postId).executeAsOneOrNull() }

  suspend fun markSeenComments(postId: String, comments: List<Comment>) {
    withContext(dbDispatcher) {
      postCommentsQueries.rememberComments(PostComments(postId, comments.map { it.shortId }))
    }
  }

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
