package dev.msfjarvis.claw.android.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dev.msfjarvis.claw.android.injection.IODispatcher
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LobstersPagingSource
@AssistedInject
constructor(
  @Assisted private val remoteFetcher: RemoteFetcher<LobstersPost>,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    return try {
      val page = params.key ?: 1
      val posts = withContext(ioDispatcher) { remoteFetcher.getItemsAtPage(page) }

      LoadResult.Page(
        data = posts,
        prevKey = if (page == 1) null else page - 1,
        nextKey = page.plus(1)
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
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
