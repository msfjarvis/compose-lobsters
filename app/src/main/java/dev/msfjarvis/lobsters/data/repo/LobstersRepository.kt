package dev.msfjarvis.lobsters.data.repo

import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LobstersRepository @Inject constructor(private val lobstersDatabase: LobstersDatabase) {

  fun isPostSaved(postId: String): Boolean {
    // returns the post if it exists and its is_saved property is true
    val post = lobstersDatabase.postQueries.isPostSaved(postId).executeAsOneOrNull()
    return post != null
  }

  suspend fun getPost(postId: String): LobstersPost? = withContext(Dispatchers.IO) {
    return@withContext lobstersDatabase.postQueries.selectPost(postId).executeAsOneOrNull()
  }

  suspend fun getSavedPosts(): List<LobstersPost> = withContext(Dispatchers.IO) {
    return@withContext lobstersDatabase.postQueries.selectSavedPosts().executeAsList()
  }

  suspend fun addPostToDB(post: LobstersPost) = withContext(Dispatchers.IO) {
    lobstersDatabase.postQueries.insertOrReplacePost(post)
  }

  suspend fun savePost(post: LobstersPost) = withContext(Dispatchers.IO) {
    if (!isPostSaved(post.short_id)) {
      lobstersDatabase.postQueries.savePost(post.short_id)
    }
  }

  suspend fun removeSavedPost(post: LobstersPost) = withContext(Dispatchers.IO) {
    if (isPostSaved(post.short_id)) {
      lobstersDatabase.postQueries.removeSavedPost(post.short_id)
    }
  }
}
