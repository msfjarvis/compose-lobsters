package dev.msfjarvis.lobsters.data.repo

import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.database.LobstersDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class LobstersRepository constructor(
  private val lobstersApi: LobstersApi,
  private val lobstersDatabase: LobstersDatabase,
) {

  private val savedPostsCache: MutableMap<String, SavedPost> = mutableMapOf()
  private val _isCacheReady = MutableStateFlow(false)
  val isCacheReady = _isCacheReady.asStateFlow()

  fun isPostSaved(postId: String): Boolean {
    return savedPostsCache.containsKey(postId)
  }

  fun getAllPostsFromCache(): List<SavedPost> {
    return savedPostsCache.values.toList()
  }

  suspend fun fetchPosts(page: Int): List<LobstersPost> = withContext(Dispatchers.IO) {
    return@withContext lobstersApi.getHottestPosts(page)
  }

  // https://issuetracker.google.com/issues/181221325
  @Suppress("NewApi")
  suspend fun updateCache() {
    if (_isCacheReady.value) return
    val posts = getSavedPosts()

    posts.forEach {
      savedPostsCache[it.shortId] = it
    }
    _isCacheReady.value = true
  }

  private suspend fun getSavedPosts(): List<SavedPost> = withContext(Dispatchers.IO) {
    return@withContext lobstersDatabase.savedPostQueries.selectAllPosts().executeAsList()
  }

  suspend fun addPost(post: SavedPost) = withContext(Dispatchers.IO) {
    if (!savedPostsCache.containsKey(post.shortId)) {
      savedPostsCache.putIfAbsent(post.shortId, post)
      lobstersDatabase.savedPostQueries.insertOrReplacePost(post)
    }
  }

  suspend fun removePost(post: SavedPost) = withContext(Dispatchers.IO) {
    if (savedPostsCache.containsKey(post.shortId)) {
      savedPostsCache.remove(post.shortId)
      lobstersDatabase.savedPostQueries.deletePost(post.shortId)
    }
  }
}
