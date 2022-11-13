/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.worker.ContributesWorker
import com.deliveryhero.whetstone.worker.WorkerScope
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.posts.toDbModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope

/**
 * WorkManager-backed [CoroutineWorker] that gets all the posts from [SavedPostsRepository], fetches
 * their newest state using the [LobstersApi] and then writes them back to the database. This allows
 * saved posts that were saved before comment counts were added to be able to show a comment count
 * and for new-enough posts that are still getting comments to have an accurate one.
 */
@ContributesWorker
class SavedPostUpdaterWorker
@Inject
constructor(
  @ForScope(WorkerScope::class) appContext: Context,
  workerParams: WorkerParameters,
  private val savedPostsRepository: SavedPostsRepository,
  private val lobstersApi: LobstersApi,
) : CoroutineWorker(appContext, workerParams) {
  override suspend fun doWork(): Result {
    val posts = savedPostsRepository.savedPosts.first()
    supervisorScope {
      posts
        .map { post ->
          async {
            val details = lobstersApi.getPostDetails(post.shortId)
            if (details is ApiResult.Success) {
              savedPostsRepository.savePost(details.value.toDbModel())
            }
          }
        }
        .awaitAll()
    }
    return Result.success()
  }
}
