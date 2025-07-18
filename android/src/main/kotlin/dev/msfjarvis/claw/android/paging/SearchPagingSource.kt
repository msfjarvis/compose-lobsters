/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.slack.eithernet.ApiResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.PAGE_SIZE
import dev.msfjarvis.claw.android.paging.LobstersPagingSource.Companion.STARTING_PAGE_INDEX
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toUIPost
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Specialized variant of [LobstersPagingSource] to prevent hammering the Lobsters search endpoint
 * with unnecessary requests. Rather than abstract out the API call this paging source takes in the
 * relevant parameters to short-circuit when there is no query specified in order to avoid calling
 * the API at all.
 */
class SearchPagingSource
@AssistedInject
constructor(
  private val searchApi: LobstersSearchApi,
  @Assisted private val queryProvider: () -> String,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, UIPost>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UIPost> {
    val searchQuery = queryProvider()
    // If there is no query, we don't need to call the API at all.
    if (searchQuery.isEmpty()) {
      return LoadResult.Page(itemsBefore = 0, data = emptyList(), prevKey = null, nextKey = null)
    }
    val page = params.key ?: STARTING_PAGE_INDEX
    val result = withContext(ioDispatcher) { searchApi.searchPosts(searchQuery, page) }
    return when (result) {
      is ApiResult.Success -> {
        // Optimization: prevent fetching more pages if we found no items, this means
        // there is no active search query.
        val nextKey = if (result.value.isEmpty()) null else page + 1
        LoadResult.Page(
          itemsBefore = (page - 1) * PAGE_SIZE,
          data = result.value.map(LobstersPost::toUIPost),
          prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
          nextKey = nextKey,
        )
      }
      is ApiResult.Failure.NetworkFailure -> LoadResult.Error(result.error)
      is ApiResult.Failure.UnknownFailure -> LoadResult.Error(result.error)
      is ApiResult.Failure.HttpFailure -> LoadResult.Error(result.toError())
      is ApiResult.Failure.ApiFailure ->
        LoadResult.Error(IOException("API returned an invalid response"))
    }
  }

  override fun getRefreshKey(state: PagingState<Int, UIPost>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      (anchorPosition / PAGE_SIZE).coerceAtLeast(STARTING_PAGE_INDEX)
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(queryProvider: () -> String): SearchPagingSource
  }
}
