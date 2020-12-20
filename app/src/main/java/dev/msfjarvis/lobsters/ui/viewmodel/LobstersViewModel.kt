package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
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
  private val pagingSource: LobstersPagingSource,
  private val lobstersRepository: LobstersRepository,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<LobstersPost>>(emptyList())
  val savedPosts = _savedPosts.asStateFlow()
  val posts = Pager(PagingConfig(25)) {
    pagingSource
  }.flow

  fun toggleSave(post: LobstersPost) {
    viewModelScope.launch {
      val isSaved = lobstersRepository.isPostSaved(post.short_id)
      if (isSaved) removeSavedPost(post) else savePost(post)
    }
  }

  private fun savePost(post: LobstersPost) {
    viewModelScope.launch {
      lobstersRepository.savePost(post)
      _savedPosts.value = _savedPosts.value + post
    }
  }

  private fun removeSavedPost(post: LobstersPost) {
    viewModelScope.launch {
      lobstersRepository.removeSavedPost(post)
      _savedPosts.value = _savedPosts.value - post
    }
  }
}
