package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.msfjarvis.lobsters.data.remote.LobstersPagingSource
import dev.msfjarvis.lobsters.data.source.PostsDatabase
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LobstersViewModel @ViewModelInject constructor(
  private val pagingSource: LobstersPagingSource,
  database: PostsDatabase,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<LobstersPost>>(emptyList())
  private val savedPostsDao = database.savedPostsDao()
  val savedPosts: StateFlow<List<LobstersPost>> get() = _savedPosts
  val posts = Pager(PagingConfig(25)) {
    pagingSource
  }.flow

  init {
    getSavedPosts()
  }

  private fun getSavedPosts() {
    viewModelScope.launch {
      savedPostsDao.loadPosts().collectLatest { _savedPosts.value = it }
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
