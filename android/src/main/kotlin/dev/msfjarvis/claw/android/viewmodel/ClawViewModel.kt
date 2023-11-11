/*
 * Copyright © 2021-2023 Harsh Shandilya.
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
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import com.squareup.anvil.annotations.optional.ForScope
import dev.msfjarvis.claw.android.glance.SavedPostsWidget
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.PAGE_SIZE
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.STARTING_PAGE_INDEX
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.core.injection.MainDispatcher
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.Comment
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@ContributesViewModel
class ClawViewModel
@Inject
constructor(
  private val api: LobstersApi,
  val shioriRepository: ShioriRepository,
  private val searchApi: LobstersSearchApi,
  private val commentsRepository: CommentsRepository,
  private val readPostsRepository: ReadPostsRepository,
  private val savedPostsRepository: SavedPostsRepository,
  private val linkMetadataRepository: LinkMetadataRepository,
  private val dataTransferRepository: DataTransferRepository,
  private val pagingSourceFactory: LobstersPagingSource.Factory,
  private val searchPagingSourceFactory: SearchPagingSource.Factory,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
  @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
  @ForScope(ApplicationScope::class) context: Context,
) : AndroidViewModel(context as Application) {
  private val hottestPostsPager =
    Pager(PagingConfig(pageSize = PAGE_SIZE), initialKey = STARTING_PAGE_INDEX) {
      pagingSourceFactory.create(api::getHottestPosts)
    }
  private val newestPostsPager =
    Pager(PagingConfig(pageSize = PAGE_SIZE), initialKey = STARTING_PAGE_INDEX) {
      pagingSourceFactory.create(api::getNewestPosts)
    }
  private val searchResultsPager =
    Pager(PagingConfig(pageSize = PAGE_SIZE), initialKey = STARTING_PAGE_INDEX) {
      searchPagingSourceFactory.create { searchApi.searchPosts(searchQuery, it) }
    }

  val hottestPosts
    get() = hottestPostsPager.flow

  val newestPosts
    get() = newestPostsPager.flow

  val savedPosts
    get() =
      savedPostsRepository.savedPosts.map {
        it.sortedByDescending { post -> post.createdAt.toLocalDateTime() }
      }

  val savedPostsByMonth
    get() = savedPosts.map(::mapSavedPosts)

  val searchResults
    get() = searchResultsPager.flow

  var searchQuery by mutableStateOf("")

  private val _savedPostsMutex = Mutex()
  private var _savedPosts = emptyList<String>()

  init {
    viewModelScope.launch {
      savedPosts.collectLatest {
        _savedPostsMutex.withLock { _savedPosts = it.map(SavedPost::shortId) }
      }
    }
  }

  private fun mapSavedPosts(items: List<SavedPost>): ImmutableMap<Month, List<SavedPost>> {
    val sorted =
      items.sortedWith { post1, post2 ->
        post2.createdAt.toLocalDateTime().compareTo(post1.createdAt.toLocalDateTime())
      }
    return sorted.groupBy { post -> post.createdAt.toLocalDateTime().month }.toImmutableMap()
  }

  fun isPostSaved(post: SavedPost): Boolean {
    return _savedPosts.contains(post.shortId)
  }

  fun toggleSave(post: SavedPost) {
    viewModelScope.launch(ioDispatcher) {
      val saved = isPostSaved(post)
      if (saved) {
        savedPostsRepository.removePost(post)
      } else {
        savedPostsRepository.savePost(post)
      }
      val newPosts = savedPosts.first()
      withContext(mainDispatcher) {
        SavedPostsWidget(newPosts.take(50)).updateAll(getApplication())
      }
    }
  }

  suspend fun getPostComments(postId: String) =
    withContext(ioDispatcher) {
      when (val result = api.getPostDetails(postId)) {
        is Success -> result.value
        is Failure.NetworkFailure -> throw result.error
        is Failure.UnknownFailure -> throw result.error
        is Failure.HttpFailure -> {
          if (result.code == HttpURLConnection.HTTP_NOT_FOUND) {
            throw IOException("Story was removed by moderator")
          } else {
            throw IOException("API returned an invalid response")
          }
        }
        is Failure.ApiFailure -> throw IOException("API returned an invalid response")
      }
    }

  suspend fun getSeenComments(postId: String) = commentsRepository.getSeenComments(postId)

  fun markSeenComments(postId: String, comments: List<Comment>) {
    viewModelScope.launch { commentsRepository.markSeenComments(postId, comments) }
  }

  suspend fun getLinkMetadata(url: String) =
    withContext(ioDispatcher) { linkMetadataRepository.getLinkMetadata(url) }

  suspend fun getUserProfile(username: String) =
    withContext(ioDispatcher) {
      when (val result = api.getUser(username)) {
        is Success -> result.value
        is Failure.NetworkFailure -> throw result.error
        is Failure.UnknownFailure -> throw result.error
        is Failure.HttpFailure,
        is Failure.ApiFailure -> throw IOException("API returned an invalid response")
      }
    }

  suspend fun importPosts(input: InputStream) = dataTransferRepository.importPosts(input)

  suspend fun exportPosts(output: OutputStream) = dataTransferRepository.exportPosts(output)

  fun markPostAsRead(postId: String) {
    viewModelScope.launch { readPostsRepository.markRead(postId) }
  }

  suspend fun isPostRead(postId: String) = readPostsRepository.isRead(postId)

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
