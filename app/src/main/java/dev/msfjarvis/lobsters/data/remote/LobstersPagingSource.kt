package dev.msfjarvis.lobsters.data.remote

import androidx.paging.PagingSource
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.model.LobstersPost
import javax.inject.Inject

class LobstersPagingSource @Inject constructor(
  private val lobstersApi: LobstersApi,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    return try {
      val page = params.key ?: 1
      val posts = lobstersApi.getHottestPosts(page)

      LoadResult.Page(
        data = posts,
        prevKey = if (page == 1) null else page - 1,
        nextKey = page.plus(1)
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}
