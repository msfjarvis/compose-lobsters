package dev.msfjarvis.lobsters.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.data.preferences.ClawPreferences
import dev.msfjarvis.lobsters.data.remote.LobstersPagingSource
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.LobstersPostDetails
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class LobstersViewModel
@Inject
constructor(
  private val lobstersRepository: LobstersRepository,
  private val clawPreferences: ClawPreferences,
) : ViewModel() {
  private val _savedPosts = MutableStateFlow<List<SavedPost>>(emptyList())
  val savedPosts = _savedPosts.asStateFlow()
  val hottestPosts =
    Pager(PagingConfig(25)) {
        LobstersPagingSource(getPostFetcher(hottest = true)).also { hottestPostsPagingSource = it }
      }
      .flow
      .cachedIn(viewModelScope)
  val newestPosts =
    Pager(PagingConfig(25)) {
        LobstersPagingSource(getPostFetcher(newest = true)).also { newestPostsPagingSource = it }
      }
      .flow
      .cachedIn(viewModelScope)
  private var hottestPostsPagingSource: LobstersPagingSource? = null
  private var newestPostsPagingSource: LobstersPagingSource? = null

  init {
    viewModelScope.launch { lobstersRepository.updateCache() }
    lobstersRepository
      .isCacheReady
      .onEach { ready ->
        if (ready) {
          _savedPosts.value = lobstersRepository.getAllPostsFromCache()
        }
      }
      .launchIn(viewModelScope)
  }

  fun getSortOrder(): Flow<Boolean> {
    return clawPreferences.sortingOrder
  }

  suspend fun toggleSortOrder() {
    clawPreferences.toggleSortingOrder()
  }

  suspend fun getPostDetails(postId: String): LobstersPostDetails {
    return lobstersRepository.fetchPostDetails(postId)
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

  /**
   * Returns a lambda that can be used to fetch the next round of hottest or newest posts. Having a
   * single paging source makes it significantly easier to make changes to the logic both here and
   * in [LobstersPagingSource].
   */
  private fun getPostFetcher(
    newest: Boolean = false,
    hottest: Boolean = false,
  ): suspend (page: Int) -> List<LobstersPost> {
    check(newest != hottest) { "Must pick one of newest or hottest" }
    return { page ->
      if (newest) {
        lobstersRepository.fetchNewestPosts(page)
      } else {
        lobstersRepository.fetchHottestPosts(page)
      }
    }
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
