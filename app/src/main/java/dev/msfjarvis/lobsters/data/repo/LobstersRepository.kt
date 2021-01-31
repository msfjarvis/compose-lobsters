package dev.msfjarvis.lobsters.data.repo

import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LobstersRepository @Inject constructor(
  private val lobstersApi: LobstersApi,
  private val lobstersDatabase: LobstersDatabase,
) {

  private val savedPostsCache: MutableMap<String, LobstersPost> = mutableMapOf()
  private val _isCacheReady = MutableStateFlow(false)
  val isCacheReady = _isCacheReady.asStateFlow()

  fun isPostSaved(postId: String): Boolean {
    return savedPostsCache.containsKey(postId)
  }

  fun getPostFromCache(postId: String): LobstersPost? {
    return savedPostsCache[postId]
  }

  fun getAllPostsFromCache(): List<LobstersPost> {
    return savedPostsCache.values.toList()
  }

  suspend fun fetchPosts(page: Int): List<LobstersPost> = withContext(Dispatchers.IO) {
    return@withContext lobstersApi.getHottestPosts(page)
  }

  suspend fun updateCache() {
    if (_isCacheReady.value) return
    val posts = getAllPosts()

    posts.forEach {
      savedPostsCache.putIfAbsent(it.short_id, it)
    }
    _isCacheReady.value = true
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
      lobstersDatabase.postQueries.insertOrReplacePost(post)
    }
  }

  suspend fun removePost(post: LobstersPost) = withContext(Dispatchers.IO) {
    if (savedPostsCache.containsKey(post.short_id)) {
      savedPostsCache.remove(post.short_id)
      lobstersDatabase.postQueries.deletePost(post.short_id)
    }
  }
}
