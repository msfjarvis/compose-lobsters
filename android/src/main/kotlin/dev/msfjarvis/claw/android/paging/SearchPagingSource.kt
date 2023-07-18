/*
 * Copyright © 2021-2023 Harsh Shandilya.
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
import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.model.LobstersPost
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Duplicate of [LobstersPagingSource] with an additional optimization to not continue loading
 * further pages if nothing is found in the current one. This is required to prevent the app from
 * hammering the search endpoint with up to 70 consecutive requests for the search results of a
 * blank query.
 */
class SearchPagingSource
@AssistedInject
constructor(
  @Assisted private val remoteFetcher: RemoteFetcher<LobstersPost>,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, LobstersPost>() {
  override fun getRefreshKey(state: PagingState<Int, LobstersPost>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      (anchorPosition / PAGE_SIZE).coerceAtLeast(STARTING_PAGE_INDEX)
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    val page = params.key ?: STARTING_PAGE_INDEX
    return when (val result = withContext(ioDispatcher) { remoteFetcher.getItemsAtPage(page) }) {
      is ApiResult.Success -> {
        // Optimization: prevent fetching more pages if we found no items, this means
        // there is no active search query.
        val nextKey = if (result.value.isEmpty()) null else page + 1
        LoadResult.Page(
          itemsBefore = (page - 1) * PAGE_SIZE,
          data = result.value,
          prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
          nextKey = nextKey,
        )
      }
      is ApiResult.Failure.NetworkFailure -> LoadResult.Error(result.error)
      is ApiResult.Failure.UnknownFailure -> LoadResult.Error(result.error)
      is ApiResult.Failure.HttpFailure,
      is ApiResult.Failure.ApiFailure ->
        LoadResult.Error(IOException("API returned an invalid response"))
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(remoteFetcher: RemoteFetcher<LobstersPost>): SearchPagingSource
  }
}
