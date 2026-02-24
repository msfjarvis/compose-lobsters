/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.msfjarvis.claw.common.tags.TagFilterRepository
import dev.msfjarvis.claw.core.injection.InjectedWorkerFactory
import dev.msfjarvis.claw.core.injection.WorkerKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding

@AssistedInject
class TagExpirationCleanupWorker(
  context: Context,
  @Assisted params: WorkerParameters,
  private val tagFilterRepository: TagFilterRepository,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    tagFilterRepository.removeExpiredTags()
    return Result.success()
  }

  @WorkerKey(TagExpirationCleanupWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<InjectedWorkerFactory.WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory : InjectedWorkerFactory.WorkerInstanceFactory<TagExpirationCleanupWorker>
}
