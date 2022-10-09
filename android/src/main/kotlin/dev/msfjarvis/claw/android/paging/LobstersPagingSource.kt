package dev.msfjarvis.claw.android.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.msfjarvis.claw.android.injection.IODispatcher
import dev.msfjarvis.claw.model.LobstersPost
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LobstersPagingSource
@AssistedInject
constructor(
  @Assisted private val remoteFetcher: RemoteFetcher<LobstersPost>,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    val page = params.key ?: 1
    return when (val result = withContext(ioDispatcher) { remoteFetcher.getItemsAtPage(page) }) {
      is Success ->
        LoadResult.Page(
          data = result.value,
          prevKey = if (page == 1) null else page - 1,
          nextKey = page.plus(1)
        )
      is Failure.NetworkFailure -> LoadResult.Error(result.error)
      is Failure.UnknownFailure -> LoadResult.Error(result.error)
      is Failure.HttpFailure,
      is Failure.ApiFailure -> LoadResult.Error(IOException("API returned an invalid response"))
    }
  }

  override fun getRefreshKey(state: PagingState<Int, LobstersPost>): Int {
    return state.pages.size + 1
  }

  @AssistedFactory
  interface Factory {
    fun create(remoteFetcher: RemoteFetcher<LobstersPost>): LobstersPagingSource
  }
}
