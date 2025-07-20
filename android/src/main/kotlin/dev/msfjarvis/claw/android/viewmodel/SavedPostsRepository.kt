/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.database.local.SavedPostQueries
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toSavedPost
import dev.zacsweers.metro.Inject
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SavedPostsRepository
@Inject
constructor(
  private val savedPostQueries: SavedPostQueries,
  @DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  val savedPosts = savedPostQueries.selectAllPosts().asFlow().mapToList(readDispatcher)

  suspend fun toggleSave(post: UIPost) {
    if (savedPosts.firstOrNull().orEmpty().any { it.shortId == post.shortId }) {
      Napier.d(tag = TAG) { "Removing post: ${post.shortId}" }
      withContext(writeDispatcher) { savedPostQueries.deletePost(post.shortId) }
    } else {
      Napier.d(tag = TAG) { "Saving post: ${post.shortId}" }
      withContext(writeDispatcher) { savedPostQueries.insertOrReplacePost(post.toSavedPost()) }
    }
  }

  suspend fun savePosts(posts: List<SavedPost>) {
    Napier.d(tag = TAG) { "Saving posts: ${posts.joinToString(",") { it.shortId }}" }
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
