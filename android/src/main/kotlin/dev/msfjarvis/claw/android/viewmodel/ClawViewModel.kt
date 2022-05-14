package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.ui.asZonedDateTime
import dev.msfjarvis.claw.api.LobstersApi
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
  private var hottestPostsPagingSource: LobstersPagingSource? = null
  private var newestPostsPagingSource: LobstersPagingSource? = null
  private val hottestPostsPager =
    Pager(PagingConfig(20)) {
      LobstersPagingSource(api::getHottestPosts).also { hottestPostsPagingSource = it }
    }
  private val newestPostsPager =
    Pager(PagingConfig(20)) {
      LobstersPagingSource(api::getNewestPosts).also { newestPostsPagingSource = it }
    }

  val hottestPosts
    get() = hottestPostsPager.flow

  val newestPosts
    get() = newestPostsPager.flow

  private val savedPostsFlow
    get() = repository.savedPosts

  val savedPosts
    get() = savedPostsFlow.map(::mapSavedPosts)

  private fun mapSavedPosts(items: List<SavedPost>): Map<Month, List<SavedPost>> {
    val sorted = items.sortedByDescending { post -> post.createdAt.asZonedDateTime() }
    return sorted.groupBy { post -> post.createdAt.asZonedDateTime().month }
  }

  suspend fun isPostSaved(post: SavedPost): Boolean {
    return savedPostsFlow.first().any { savedPost -> savedPost.shortId == post.shortId }
  }

  fun toggleSave(post: SavedPost) {
    viewModelScope.launch {
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
    hottestPostsPagingSource?.invalidate()
  }

  fun refreshNewestPosts() {
    newestPostsPagingSource?.invalidate()
  }
}
