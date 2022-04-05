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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first

/**
 * WorkManager-backed [CoroutineWorker] that gets all the posts from [SavedPostsRepository], fetches
 * their newest state using the [LobstersApi] and then writes them back to the database. This allows
 * saved posts that were saved before comment counts were added to be able to show a comment count
 * and for new-enough posts that are still getting comments to have an accurate one.
 */
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
    savedPostsRepository
      .savedPosts
      .first()
      .mapNotNull { post -> runCatching { lobstersApi.getPostDetails(post.shortId) }.getOrNull() }
      .map { postDetails -> postDetails.toDbModel() }
      .map { post -> savedPostsRepository.savePost(post) }
    return Result.success()
  }
}
