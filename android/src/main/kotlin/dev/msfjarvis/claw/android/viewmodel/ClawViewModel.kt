package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.database.local.SavedPost
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClawViewModel
@Inject
constructor(
  api: LobstersApi,
  private val repository: SavedPostsRepository,
) : ViewModel() {
  var lastPagingSource: LobstersPagingSource? = null
  private val savedPosts = flow { repository.savedPosts.collect { emit(it) } }
  private val pager =
    Pager(PagingConfig(20)) {
      LobstersPagingSource(api::getHottestPosts).also { lastPagingSource = it }
    }

  val pagerFlow
    get() = pager.flow

  suspend fun isPostSaved(post: SavedPost): Boolean {
    return savedPosts.mapLatest { posts -> post in posts }.last()
  }

  suspend fun toggleSave(post: SavedPost) {
    val saved = isPostSaved(post)
    println("saved=$saved")
    if (saved) {
      repository.removePost(post)
    } else {
      repository.savePost(post)
    }
  }

  fun reloadPosts() {
    lastPagingSource?.invalidate()
  }
}
