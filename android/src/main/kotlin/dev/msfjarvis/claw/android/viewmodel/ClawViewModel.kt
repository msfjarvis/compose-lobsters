package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.claw.android.ui.asZonedDateTime
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.paging.LobstersPagination
import dev.msfjarvis.claw.database.local.SavedPost
import java.time.Month
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ClawViewModel
@Inject
constructor(
  private val api: LobstersApi,
  private val repository: SavedPostsRepository,
) : ViewModel() {
  private companion object {
    const val LobstersPageSize = 25
  }
  private val hottestPostsPager =
    LobstersPagination(viewModelScope, emptyList()) { currentList ->
      val currentPage = currentList.size / LobstersPageSize
      api.getHottestPosts(currentPage + 1)
    }
  private val newestPostsPager =
    LobstersPagination(viewModelScope, emptyList()) { currentList ->
      val currentPage = currentList.size / LobstersPageSize
      api.getNewestPosts(currentPage + 1)
    }

  val hottestPosts
    get() = hottestPostsPager.pagingResultFlow

  val newestPosts
    get() = newestPostsPager.pagingResultFlow

  private val savedPostsFlow
    get() = repository.savedPosts

  val savedPosts
    get() = savedPostsFlow.map(::mapSavedPosts)

  init {
    hottestPostsPager.loadFirstPage()
    newestPostsPager.loadFirstPage()
  }

  private fun mapSavedPosts(items: List<SavedPost>): Map<Month, List<SavedPost>> {
    val sorted = items.sortedByDescending { post -> post.createdAt.asZonedDateTime() }
    return sorted.groupBy { post -> post.createdAt.asZonedDateTime().month }
  }

  suspend fun isPostSaved(post: SavedPost): Boolean {
    return savedPostsFlow.first().any { savedPost -> savedPost.shortId == post.shortId }
  }

  fun toggleSave(post: SavedPost) {
    viewModelScope.launch(Dispatchers.IO) {
      val saved = isPostSaved(post)
      if (saved) {
        repository.removePost(post)
      } else {
        repository.savePost(post)
      }
    }
  }

  suspend fun getPostComments(postId: String) =
    withContext(Dispatchers.IO) { api.getPostDetails(postId) }

  suspend fun getUserProfile(username: String) =
    withContext(Dispatchers.IO) { api.getUser(username) }

  fun refreshHottestPosts() {
    hottestPostsPager.refresh()
  }

  fun refreshNewestPosts() {
    newestPostsPager.refresh()
  }

  fun loadHottestPosts() {
    hottestPostsPager.loadNextPage()
  }

  fun loadNewestPosts() {
    newestPostsPager.loadNextPage()
  }
}
