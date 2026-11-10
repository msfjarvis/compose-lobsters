/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.feature.posts.api.PostDataTransfer
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.PAGE_SIZE
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.STARTING_PAGE_INDEX
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.msfjarvis.claw.feature.posts.api.SavedPostsUpdateNotifier
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.InputStream
import java.io.OutputStream
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class PostsViewModel(
  private val api: LobstersApi,
  private val pagingSourceFactory: LobstersPagingSource.Factory,
  private val readPostsRepository: ReadPostsRepository,
  private val savedPosts: SavedPosts,
  private val savedPostsUpdateNotifier: SavedPostsUpdateNotifier,
  private val linkMetadataRepository: LinkMetadataRepository,
  private val postDataTransfer: PostDataTransfer,
) : ViewModel() {
  val hottestPosts: Flow<PagingData<UIPost>> by lazy {
    Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = {
          pagingSourceFactory.create { page -> api.getHottestPosts(page) }
        },
      )
      .flow
      .cachedIn(viewModelScope)
  }

  val newestPosts: Flow<PagingData<UIPost>> by lazy {
    Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = {
          pagingSourceFactory.create { page -> api.getNewestPosts(page) }
        },
      )
      .flow
      .cachedIn(viewModelScope)
  }

  val savedPostsCount = savedPosts.savedPostsCount
  val savedPostsByMonth =
    savedPosts.savedPostsSortedByDate.map { posts ->
      posts
        .groupBy { post ->
          val time = post.createdAt.toLocalDateTime()
          "${time.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${time.year}"
        }
        .toImmutableMap()
    }

  private var readPosts by mutableStateOf(emptySet<String>())
  private var savedPostIds by mutableStateOf(emptySet<String>())

  init {
    viewModelScope.launch {
      savedPosts.savedPostIds.collectLatest { savedPostIds = it.toSet() }
    }
    viewModelScope.launch {
      readPostsRepository.initialize()
      readPostsRepository.readPosts.collectLatest { readPosts = it }
    }
  }

  fun toggleSave(post: UIPost) {
    viewModelScope.launch {
      savedPosts.toggleSave(post)
      savedPostsUpdateNotifier.onSavedPostsUpdated()
    }
  }

  fun isPostRead(post: UIPost): Boolean = post.shortId in readPosts

  fun isPostSaved(post: UIPost): Boolean = post.shortId in savedPostIds

  suspend fun getLinkMetadata(url: String): LinkMetadata =
    linkMetadataRepository.getLinkMetadata(url)

  suspend fun importPosts(input: InputStream): Result<Unit> = postDataTransfer.importPosts(input)

  suspend fun exportPostsAsJson(output: OutputStream) {
    postDataTransfer.exportPostsAsJson(output)
  }

  fun markPostAsRead(postId: String) {
    viewModelScope.launch { readPostsRepository.markRead(postId) }
  }

  /** Parses only timestamps in the format returned by the Lobsters API. */
  private fun String.toLocalDateTime(): LocalDateTime {
    if (isEmpty()) return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
  }
}
