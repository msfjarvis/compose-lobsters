package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.source.PostsDatabase
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LobstersViewModel @ViewModelInject constructor(
  private val lobstersApi: LobstersApi,
  database: PostsDatabase,
) : ViewModel() {
  private var apiPage = 1
  private val _posts = MutableStateFlow<List<LobstersPost>>(emptyList())
  private val _savedPosts = MutableStateFlow<List<LobstersPost>>(emptyList())
  private val postsDao = database.postsDao()
  private val savedPostsDao = database.savedPostsDao()
  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    when (throwable) {
      // Swallow known network errors that can be recovered from.
      is UnknownHostException, is SocketTimeoutException -> {
        if (_posts.value.isEmpty()) {
          viewModelScope.launch {
            postsDao.loadPosts().collectLatest { _posts.value = it }
          }
        }
      }
      else -> throw throwable
    }
  }
  val posts: StateFlow<List<LobstersPost>> get() = _posts
  val savedPosts: StateFlow<List<LobstersPost>> get() = _savedPosts

  init {
    getMorePostsInternal(true)
    getSavedPosts()
  }

  private fun getSavedPosts() {
    viewModelScope.launch {
      savedPostsDao.loadPosts().collectLatest { _savedPosts.value = it }
    }
  }

  fun getMorePosts() {
    getMorePostsInternal(false)
  }

  private fun getMorePostsInternal(firstLoad: Boolean) {
    viewModelScope.launch(coroutineExceptionHandler) {
      val newPosts = lobstersApi.getHottestPosts(apiPage)
      if (firstLoad) {
        _posts.value = newPosts
        postsDao.deleteAllPosts()
      } else {
        _posts.value += newPosts
      }
      apiPage += 1
      postsDao.insertPosts(*_posts.value.toTypedArray())
    }
  }

  fun savePost(post: LobstersPost) {
    viewModelScope.launch {
      savedPostsDao.insertPosts(post)
      getSavedPosts()
    }
  }

  fun removeSavedPost(post: LobstersPost) {
    viewModelScope.launch {
      savedPostsDao.deletePostById(post.shortId)
      getSavedPosts()
    }
  }
}
