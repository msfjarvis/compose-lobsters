package dev.msfjarvis.lobsters.data.repo

import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LobstersRepository @Inject constructor(private val lobstersDatabase: LobstersDatabase) {

  private val savedPostsCache: MutableMap<String, LobstersPost> = mutableMapOf()
  private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

  init {
    coroutineScope.launch {
      getAllPosts().forEach {
        savedPostsCache.putIfAbsent(it.short_id, it)
      }
    }
  }

  fun isPostSaved(postId: String): Boolean {
    return savedPostsCache.containsKey(postId)
  }

  fun getPostFromCache(postId: String): LobstersPost? {
    return savedPostsCache[postId]
  }

  fun getAllPostsFromCache(): List<LobstersPost> {
    return savedPostsCache.values.toList()
  }

  private suspend fun getPost(postId: String): LobstersPost? = withContext(Dispatchers.IO) {
    return@withContext lobstersDatabase.postQueries.selectPost(postId).executeAsOneOrNull()
  }

  private suspend fun getAllPosts(): List<LobstersPost> = withContext(Dispatchers.IO) {
    return@withContext lobstersDatabase.postQueries.selectAllPosts().executeAsList()
  }

  suspend fun addPost(post: LobstersPost) = withContext(Dispatchers.IO) {
    if (!savedPostsCache.containsKey(post.short_id)) {
      savedPostsCache.putIfAbsent(post.short_id, post)
      lobstersDatabase.postQueries.insertOrReplacePost(post.copy(is_saved = true))
    }
  }

  suspend fun removePost(post: LobstersPost) = withContext(Dispatchers.IO) {
    if (savedPostsCache.containsKey(post.short_id)) {
      savedPostsCache.remove(post.short_id)
      lobstersDatabase.postQueries.removeSavedPost(post.short_id)
    }
  }
}
