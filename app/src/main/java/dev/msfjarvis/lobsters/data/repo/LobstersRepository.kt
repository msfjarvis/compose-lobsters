package dev.msfjarvis.lobsters.data.repo

import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LobstersRepository @Inject constructor(private val lobstersDatabase: LobstersDatabase) {

  private var savedPostCache: MutableSet<String>? = null

  fun isPostSaved(postId: String): Boolean {
    savedPostCache ?: return false
    return requireNotNull(savedPostCache).contains(postId)
  }

  suspend fun savePost(post: LobstersPost) = withContext(Dispatchers.IO) {
    val isElementAdded = getCachedPosts().add(post.short_id)
    if (isElementAdded) {
      lobstersDatabase.postQueries.savePost(post.short_id)
    }
  }

  suspend fun removeSavedPost(post: LobstersPost) = withContext(Dispatchers.IO) {
    val isElementRemoved = getCachedPosts().remove(post.short_id)
    if (isElementRemoved) {
      lobstersDatabase.postQueries.removeSavedPost(post.short_id)
    }
  }

  suspend fun getCachedPosts(): MutableSet<String> = withContext(Dispatchers.IO) {
    if (savedPostCache != null) return@withContext requireNotNull(savedPostCache)

    val dbPosts = lobstersDatabase.postQueries.selectSavedPosts().executeAsList()
    savedPostCache = dbPosts.filter { it.is_saved ?: false }.map { it.short_id }.toMutableSet()
    return@withContext requireNotNull(savedPostCache)
  }
}
