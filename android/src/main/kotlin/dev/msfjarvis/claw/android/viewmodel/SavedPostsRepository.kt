package dev.msfjarvis.claw.android.viewmodel

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
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
    println("Saving post: ${post.shortId}")
    withContext(Dispatchers.IO) { savedPostQueries.insertOrReplacePost(post) }
  }

  suspend fun removePost(post: SavedPost) {
    println("Removing post: ${post.shortId}")
    withContext(Dispatchers.IO) { savedPostQueries.deletePost(post.shortId) }
  }
}
