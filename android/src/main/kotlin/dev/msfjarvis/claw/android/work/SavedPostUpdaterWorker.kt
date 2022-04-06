package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.posts.toDbModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first

/**
 * WorkManager-backed [CoroutineWorker] that gets all the posts from [SavedPostsRepository], fetches
 * their newest state using the [LobstersApi] and then writes them back to the database. This allows
 * saved posts that were saved before comment counts were added to be able to show a comment count
 * and for new-enough posts that are still getting comments to have an accurate one.
 */
@Suppress("DEPRECATION") // We're being nasty
@OptIn(ExperimentalCoroutinesApi::class)
@HiltWorker
class SavedPostUpdaterWorker
@AssistedInject
constructor(
  private val savedPostsRepository: SavedPostsRepository,
  private val lobstersApi: LobstersApi,
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {
  override suspend fun doWork(): Result {
    val posts = savedPostsRepository.savedPosts.first()
    posts
      .map { post ->
        CoroutineScope(coroutineContext + Job()).async {
          val details = runCatching { lobstersApi.getPostDetails(post.shortId) }.getOrNull()
          if (details != null) {
            savedPostsRepository.savePost(details.toDbModel())
          }
        }
      }
      .awaitAll()
    return Result.success()
  }
}
