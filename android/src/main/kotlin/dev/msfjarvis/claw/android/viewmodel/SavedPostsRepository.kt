package dev.msfjarvis.claw.android.viewmodel

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.SavedPost
import io.github.aakira.napier.Napier
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SavedPostsRepository
@Inject
constructor(
  database: LobstersDatabase,
) {
  private val savedPostQueries = database.savedPostQueries
  val savedPosts = savedPostQueries.selectAllPosts().asFlow().mapToList()

  suspend fun savePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Saving post: ${post.shortId}" }
    withContext(Dispatchers.IO) { savedPostQueries.insertOrReplacePost(post) }
  }

  suspend fun removePost(post: SavedPost) {
    Napier.d(tag = TAG) { "Removing post: ${post.shortId}" }
    withContext(Dispatchers.IO) { savedPostQueries.deletePost(post.shortId) }
  }

  private companion object {
    private const val TAG = "SavedPostsRepository"
  }
}
