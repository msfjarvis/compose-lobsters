package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.data.remote.LobstersPagingSource
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class LobstersViewModel @Inject constructor(
  private val lobstersRepository: LobstersRepository,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<SavedPost>>(emptyList())
  val savedPosts = _savedPosts.asStateFlow()
  val posts = Pager(PagingConfig(25)) {
    LobstersPagingSource(lobstersRepository).also { pagingSource = it }
  }.flow.cachedIn(viewModelScope)
  private var pagingSource: LobstersPagingSource? = null

  init {
    lobstersRepository.isCacheReady.onEach { ready ->
      if (ready) {
        _savedPosts.value = lobstersRepository.getAllPostsFromCache()
      }
    }.launchIn(viewModelScope)
  }

  fun reloadPosts() {
    pagingSource?.invalidate()
  }

  fun toggleSave(post: SavedPost) {
    viewModelScope.launch {
      val isSaved = lobstersRepository.isPostSaved(post.shortId)
      if (isSaved) removeSavedPost(post) else savePost(post)
    }
  }

  fun isPostSaved(postId: String): Boolean {
    return lobstersRepository.isPostSaved(postId)
  }

  private fun savePost(post: SavedPost) {
    viewModelScope.launch {
      lobstersRepository.addPost(post)
      _savedPosts.value = lobstersRepository.getAllPostsFromCache()
    }
  }

  private fun removeSavedPost(post: SavedPost) {
    viewModelScope.launch {
      lobstersRepository.removePost(post)
      _savedPosts.value = lobstersRepository.getAllPostsFromCache()
    }
  }
}
