/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.android.injection.DatabaseDispatcher
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.SavedPost
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
  val savedPosts = savedPostQueries.selectAllPosts().asFlow().mapToList(dbDispatcher)

  suspend fun savePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Saving post: ${post.shortId}" }
    withContext(dbDispatcher) { savedPostQueries.insertOrReplacePost(post) }
  }

  suspend fun removePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Removing post: ${post.shortId}" }
    withContext(dbDispatcher) { savedPostQueries.deletePost(post.shortId) }
  }

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
