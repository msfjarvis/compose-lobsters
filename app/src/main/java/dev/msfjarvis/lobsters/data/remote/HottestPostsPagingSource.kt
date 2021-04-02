package dev.msfjarvis.lobsters.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import dev.msfjarvis.lobsters.model.LobstersPost

class HottestPostsPagingSource(
  private val lobstersRepository: LobstersRepository,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    return try {
      val page = params.key ?: 1
      // Update cache before fetching a list.
      // This is done to make sure that we can update the isSaved status of incoming posts.
      lobstersRepository.updateCache()
      val posts = lobstersRepository.fetchHottestPosts(page)

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
}
