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
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.android.glance.SavedPostsWidget
import dev.msfjarvis.claw.android.viewmodel.SavedPostsRepository
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.core.injection.InjectedWorkerFactory
import dev.msfjarvis.claw.core.injection.WorkerKey
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.toSavedPost
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

/**
 * WorkManager-backed [CoroutineWorker] that gets all the posts from [SavedPostsRepository] that
 * were created in the last 30 days, fetches their newest state using the [LobstersApi] and then
 * writes them back to the database. API calls are staggered with a configurable delay to avoid rate
 * limiting. This allows saved posts to stay up-to-date with current comment counts and other
 * metadata changes.
 */
@AssistedInject
class SavedPostUpdaterWorker(
  context: Context,
  @Assisted params: WorkerParameters,
  private val savedPostsRepository: SavedPostsRepository,
  private val lobstersApi: LobstersApi,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    val postsToUpdate = savedPostsRepository.getPostsFromLastNDays(DAYS_TO_UPDATE).first()

    if (postsToUpdate.isEmpty()) {
      return Result.success()
    }

    val updatedPosts = mutableListOf<SavedPost>()

    for ((index, post) in postsToUpdate.withIndex()) {
      when (val result = lobstersApi.getPostDetails(post.shortId)) {
        is Success -> {
          updatedPosts.add(result.value.toSavedPost())
        }
        else -> {}
      }

      if (index < postsToUpdate.lastIndex) {
        // Add a random delay between API calls to avoid rate limiting
        val delayMs = Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS)
        delay(delayMs)
      }
    }

    if (updatedPosts.isNotEmpty()) {
      savedPostsRepository.savePosts(updatedPosts)
      SavedPostsWidget().updateAll(applicationContext)
    }

    return Result.success()
  }

  private companion object {
    const val DAYS_TO_UPDATE = 30L
    const val MIN_DELAY_MS = 500L
    const val MAX_DELAY_MS = 2000L
  }

  @WorkerKey(SavedPostUpdaterWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<InjectedWorkerFactory.WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory : InjectedWorkerFactory.WorkerInstanceFactory<SavedPostUpdaterWorker>
}
