package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.database.local.SavedPost
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClawViewModel
@Inject
constructor(
  private val api: LobstersApi,
  private val repository: SavedPostsRepository,
) : ViewModel() {
  private var lastPagingSource: LobstersPagingSource? = null
  private val pager =
    Pager(PagingConfig(20)) {
      LobstersPagingSource(api::getHottestPosts).also { lastPagingSource = it }
    }

  val pagerFlow
    get() = pager.flow

  val savedPosts
    get() = repository.savedPosts

  suspend fun isPostSaved(post: SavedPost): Boolean {
    return savedPosts
      .mapLatest { posts -> posts.map { it.shortId } }
      .mapLatest { shortIds -> post.shortId in shortIds }
      .first()
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

  fun reloadPosts() {
    lastPagingSource?.invalidate()
  }
}
