package dev.msfjarvis.lobsters.data.remote

import androidx.paging.PagingSource
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import javax.inject.Inject

class LobstersPagingSource @Inject constructor(
  private val lobstersApi: LobstersApi,
  private val lobstersRepository: LobstersRepository,
) : PagingSource<Int, LobstersPost>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LobstersPost> {
    return try {
      val page = params.key ?: 1
      val savedPosts = lobstersRepository.getCachedPosts()
      val posts = lobstersApi.getHottestPosts(page).map { post ->
        post.copy(is_saved = savedPosts.contains(post.short_id))
      }

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
