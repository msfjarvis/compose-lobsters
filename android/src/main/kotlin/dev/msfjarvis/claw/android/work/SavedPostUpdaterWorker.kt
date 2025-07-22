/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deliveryhero.whetstone.worker.ContributesWorker
import com.deliveryhero.whetstone.worker.WorkerScope
import com.slack.eithernet.ApiResult.Success
import com.squareup.anvil.annotations.optional.ForScope
import dev.msfjarvis.claw.android.glance.SavedPostsWidget
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromSavedPost
import dev.msfjarvis.claw.model.toSavedPost
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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
    val postsToUpdate =
      savedPostsRepository.savedPosts
        .first()
        .sortedBy { post ->
          Instant.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(post.createdAt))
        }
        .take(30)

    val updatedPosts = mutableListOf<dev.msfjarvis.claw.database.local.SavedPost>()

    for (post in postsToUpdate) {
      when (val result = lobstersApi.getPostDetails(post.shortId)) {
        is Success -> {
          updatedPosts.add(result.value.toSavedPost())
        }
        else -> {
          // Continue to the next post if the API call fails
        }
      }
      // Add a 1-second delay to avoid rate-limiting
      delay(1000)
    }

    savedPostsRepository.savePosts(updatedPosts)

    SavedPostsWidget(
        savedPostsRepository.savedPosts.map { it.map(UIPost.Companion::fromSavedPost) }
      )
      .updateAll(applicationContext)
    return Result.success()
  }
}
