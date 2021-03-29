package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.data.preferences.ClawPreferences
import dev.msfjarvis.lobsters.data.remote.HottestPostsPagingSource
import dev.msfjarvis.lobsters.data.remote.NewestPostsPagingSource
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class LobstersViewModel @Inject constructor(
  private val lobstersRepository: LobstersRepository,
  private val clawPreferences: ClawPreferences,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<SavedPost>>(emptyList())
  val savedPosts = _savedPosts.asStateFlow()
  val hottestPosts = Pager(PagingConfig(25)) {
    HottestPostsPagingSource(lobstersRepository).also { hottestPostsPagingSource = it }
  }.flow.cachedIn(viewModelScope)
  val newestPosts = Pager(PagingConfig(25)) {
    NewestPostsPagingSource(lobstersRepository).also { newestPostsPagingSource = it }
  }.flow.cachedIn(viewModelScope)
  private var hottestPostsPagingSource: HottestPostsPagingSource? = null
  private var newestPostsPagingSource: NewestPostsPagingSource? = null

  init {
    lobstersRepository.isCacheReady.onEach { ready ->
      if (ready) {
        _savedPosts.value = lobstersRepository.getAllPostsFromCache()
      }
    }.launchIn(viewModelScope)
  }

  fun getSortOrder(): Flow<Boolean> {
    return clawPreferences.sortingOrder
  }

  suspend fun toggleSortOrder() {
    clawPreferences.toggleSortingOrder()
  }

  fun reloadHottestPosts() {
    hottestPostsPagingSource?.invalidate()
  }

  fun reloadNewestPosts() {
    newestPostsPagingSource?.invalidate()
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
