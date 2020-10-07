package dev.msfjarvis.lobsters.data

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.lobsters.api.LobstersApi
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
  private val dao = database.postsDao()
  private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    when (throwable) {
      // Swallow known network errors that can be recovered from.
      is UnknownHostException, is SocketTimeoutException -> {}
      else -> throw throwable
    }
  }
  val posts: StateFlow<List<LobstersPost>> get() = _posts

  init {
    viewModelScope.launch {
      dao.loadPosts().collectLatest { _posts.value = it }
    }
    getMorePostsInternal(true)
  }

  fun getMorePosts() {
    getMorePostsInternal(false)
  }

  private fun getMorePostsInternal(firstLoad: Boolean) {
    viewModelScope.launch(coroutineExceptionHandler) {
      val newPosts = lobstersApi.getHottestPosts(apiPage)
      if (firstLoad) {
        _posts.value = newPosts
        dao.deleteAllPosts()
      } else {
        _posts.value += newPosts
      }
      apiPage += 1
      dao.insertPosts(*_posts.value.toTypedArray())
    }
  }
}
