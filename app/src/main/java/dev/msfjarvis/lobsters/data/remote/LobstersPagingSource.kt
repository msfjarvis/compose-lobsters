package dev.msfjarvis.lobsters.data.remote

import android.util.Log
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
      val posts = lobstersApi.getHottestPosts(page).map { post ->
        val isSaved = lobstersRepository.isPostSaved(post.short_id)
        post.copy(is_saved = isSaved)
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
