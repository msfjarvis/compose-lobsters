package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.lobsters.data.source.PostsDatabase
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LobstersViewModel @ViewModelInject constructor(
  database: PostsDatabase,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<LobstersPost>>(emptyList())
  private val savedPostsDao = database.savedPostsDao()
  val savedPosts: StateFlow<List<LobstersPost>> get() = _savedPosts

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
