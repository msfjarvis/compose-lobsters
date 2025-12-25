/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.msfjarvis.claw.android.glance.SavedPostsWidget
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.PAGE_SIZE
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.STARTING_PAGE_INDEX
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.core.coroutines.MainDispatcher
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromSavedPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
@ViewModelKey(ClawViewModel::class)
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class ClawViewModel(
  context: Context,
  private val api: LobstersApi,
  private val readPostsRepository: ReadPostsRepository,
  private val savedPostsRepository: SavedPostsRepository,
  private val linkMetadataRepository: LinkMetadataRepository,
  private val dataTransferRepository: DataTransferRepository,
  private val pagingSourceFactory: LobstersPagingSource.Factory,
  private val searchPagingSourceFactory: SearchPagingSource.Factory,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
  @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : AndroidViewModel(context as Application) {
  val hottestPosts =
    Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = { pagingSourceFactory.create(api::getHottestPosts) },
      )
      .flow
      .cachedIn(viewModelScope)
  val newestPosts =
    Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = { pagingSourceFactory.create(api::getNewestPosts) },
      )
      .flow
      .cachedIn(viewModelScope)
  val searchResults =
    Pager(
        PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = { searchPagingSourceFactory.create { searchQuery } },
      )
      .flow
  val savedPosts = savedPostsRepository.savedPosts.map { it.map(UIPost.Companion::fromSavedPost) }
  val savedPostsCount = savedPostsRepository.savedPosts.map { it.size.toLong() }
  val savedPostsByMonth
    get() =
      savedPostsRepository.savedPostsSortedByDate.map { posts ->
        posts
          .map(UIPost.Companion::fromSavedPost)
          .groupBy { post ->
            val time = post.createdAt.toLocalDateTime()
            "${time.month.name.lowercase().capitalize(Locale.current)} ${time.year}"
          }
          .toImmutableMap()
      }

  var searchQuery by mutableStateOf("")

  private var _readPosts = emptyList<String>()
  private var _savedPosts = emptyList<String>()

  init {
    viewModelScope.launch { savedPosts.collectLatest { _savedPosts = it.map(UIPost::shortId) } }
    viewModelScope.launch { readPostsRepository.readPosts.collectLatest { _readPosts = it } }
  }

  fun toggleSave(post: UIPost) {
    viewModelScope.launch {
      savedPostsRepository.toggleSave(post)
      withContext(mainDispatcher) { SavedPostsWidget().updateAll(getApplication()) }
    }
  }

  fun isPostRead(post: UIPost): Boolean {
    return _readPosts.contains(post.shortId)
  }

  fun isPostSaved(post: UIPost): Boolean {
    return _savedPosts.contains(post.shortId)
  }

  suspend fun getLinkMetadata(url: String) =
    withContext(ioDispatcher) { linkMetadataRepository.getLinkMetadata(url) }

  suspend fun importPosts(input: InputStream): Result<Unit> =
    dataTransferRepository.importPosts(input)

  suspend fun exportPostsAsJson(output: OutputStream) =
    dataTransferRepository.exportPostsAsJson(output)

  suspend fun exportPostsAsHtml(output: OutputStream) =
    dataTransferRepository.exportPostsAsHTML(output)

  fun markPostAsRead(postId: String) {
    viewModelScope.launch { readPostsRepository.markRead(postId) }
  }

  /**
   * Parses a given [String] into a [LocalDateTime]. This method is only intended to be used for
   * dates in the format returned by the Lobsters API, and is not a general purpose parsing
   * solution.
   */
  private fun String.toLocalDateTime(): LocalDateTime {
    if (isEmpty()) return LocalDateTime.now(ZoneId.systemDefault())
    return LocalDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(this))
  }
}
