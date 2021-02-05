package dev.msfjarvis.lobsters.data.remote

import androidx.paging.PagingSource
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import javax.inject.Inject

class LobstersPagingSource @Inject constructor(
  private val lobstersRepository: LobstersRepository,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    return try {
      val page = params.key ?: 1
      // Update cache before fetching a list.
      // This is done to make sure that we can update the isSaved status of incoming posts.
      lobstersRepository.updateCache()
      val posts = lobstersRepository.fetchPosts(page)

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
