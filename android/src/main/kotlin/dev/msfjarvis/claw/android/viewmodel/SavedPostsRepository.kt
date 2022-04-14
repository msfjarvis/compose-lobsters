package dev.msfjarvis.claw.android.viewmodel

import android.util.Log
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.SavedPost
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
    Log.d("SavedPostsRepository", "Saving post: ${post.shortId}")
    withContext(Dispatchers.IO) { savedPostQueries.insertOrReplacePost(post) }
  }

  suspend fun removePost(post: SavedPost) {
    Log.d("SavedPostsRepository", "Removing post: ${post.shortId}")
    withContext(Dispatchers.IO) { savedPostQueries.deletePost(post.shortId) }
  }
}
