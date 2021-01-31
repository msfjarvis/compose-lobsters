package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.data.remote.LobstersPagingSource
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LobstersViewModel @Inject constructor(
  private val lobstersRepository: LobstersRepository,
  private val pagingSource: LobstersPagingSource,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow(lobstersRepository.getAllPostsFromCache())
  val savedPosts = _savedPosts.asStateFlow()
  val posts = Pager(PagingConfig(25)) {
    pagingSource
  }.flow.cachedIn(viewModelScope)

  fun toggleSave(post: LobstersPost) {
    viewModelScope.launch {
      val isSaved = lobstersRepository.isPostSaved(post.short_id)
      if (isSaved) removeSavedPost(post) else savePost(post)
    }
  }

  fun isPostSaved(postId: String): Boolean {
    return lobstersRepository.isPostSaved(postId)
  }

  private fun savePost(post: LobstersPost) {
    viewModelScope.launch {
      lobstersRepository.addPost(post)
      _savedPosts.value = lobstersRepository.getAllPostsFromCache()
    }
  }

  private fun removeSavedPost(post: LobstersPost) {
    viewModelScope.launch {
      lobstersRepository.removePost(post)
      _savedPosts.value = lobstersRepository.getAllPostsFromCache()
    }
  }
}
