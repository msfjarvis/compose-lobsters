/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toUIPost
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LobstersPagingSource
@AssistedInject
constructor(
  @Assisted private val remoteFetcher: RemoteFetcher<LobstersPost>,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, UIPost>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UIPost> {
    val page = params.key ?: STARTING_PAGE_INDEX
    return when (val result = withContext(ioDispatcher) { remoteFetcher.getItemsAtPage(page) }) {
      is Success ->
        LoadResult.Page(
          itemsBefore = (page - 1) * PAGE_SIZE,
          data = result.value.map(LobstersPost::toUIPost),
          prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
          nextKey = page + 1,
        )
      is Failure.NetworkFailure -> LoadResult.Error(result.error)
      is Failure.UnknownFailure -> LoadResult.Error(result.error)
      is Failure.HttpFailure -> LoadResult.Error(result.toError())
      is Failure.ApiFailure -> LoadResult.Error(IOException("API returned an invalid response"))
    }
  }

  override fun getRefreshKey(state: PagingState<Int, UIPost>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      (anchorPosition / PAGE_SIZE).coerceAtLeast(STARTING_PAGE_INDEX)
    }
  }

  @AssistedFactory
  interface Factory {
    fun create(remoteFetcher: RemoteFetcher<LobstersPost>): LobstersPagingSource
  }

  companion object {
    const val PAGE_SIZE = 25
    const val STARTING_PAGE_INDEX = 1
  }
}
