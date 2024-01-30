package dev.msfjarvis.claw.android.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.slack.eithernet.successOrNull
import dev.msfjarvis.claw.android.viewmodel.ReadPostsRepository
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class Mediator @Inject constructor(
  private val api: LobstersApi,
  private val savedPostsRepository: SavedPostsRepository,
  private val readPostsRepository: ReadPostsRepository,
) : RemoteMediator<Int, UIPost>() {

  data class RemoteKey(val label: String, val nextKey: Int)

  override suspend fun load(
    loadType: LoadType,
    state: PagingState<Int, UIPost>
  ): MediatorResult {
    try {
      // The network load method takes an optional [String] parameter. For every page
      // after the first, we pass the [String] token returned from the previous page to
      // let it continue from where it left off. For REFRESH, pass `null` to load the
      // first page.
      val loadKey = when (loadType) {
        LoadType.REFRESH -> 1
        // In this example, we never need to prepend, since REFRESH will always load the
        // first page in the list. Immediately return, reporting end of pagination.
        LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
        // Query remoteKeyDao for the next RemoteKey.
        LoadType.APPEND -> {
          val remoteKey = RemoteKey(label = "", nextKey = 1)

          // We must explicitly check if the page key is `null` when appending,
          // since `null` is only valid for initial load. If we receive `null`
          // for APPEND, that means we have reached the end of pagination and
          // there are no more items to load.
          if (remoteKey.nextKey == 1) {
            return MediatorResult.Success(endOfPaginationReached = true)
          }

          remoteKey.nextKey
        }
      }

      // Suspending network load via Retrofit. This doesn't need to be wrapped in a
      // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine CallAdapter
      // dispatches on a worker thread.
      val response = api.getHottestPosts(loadKey).successOrNull().orEmpty()

      // Store loaded data, and next key in transaction, so that they're always consistent
      database.withTransaction {
        if (loadType == LoadType.REFRESH) {
          remoteKeyDao.deleteByQuery(query)
          userDao.deleteByQuery(query)
        }

        // Update RemoteKey for this query.
        remoteKeyDao.insertOrReplace(RemoteKey(query, response.nextKey))

        // Insert new users into database, which invalidates the current
        // PagingData, allowing Paging to present the updates in the DB.
        userDao.insertAll(response.users)
      }

      MediatorResult.Success(endOfPaginationReached = response.nextKey == null)
    } catch (e: IOException) {
      MediatorResult.Error(e)
    } catch (e: HttpException) {
      MediatorResult.Error(e)
    }
    TODO()
  }
}
