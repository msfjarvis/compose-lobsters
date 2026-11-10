/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.tags.impl

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.msfjarvis.claw.core.work.api.WorkerInstanceFactory
import dev.msfjarvis.claw.core.work.api.WorkerKey
import dev.msfjarvis.claw.feature.tags.api.TagBlocks
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
  private val tagBlockRepository: TagBlocks,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    tagBlockRepository.removeExpiredTags()
    return Result.success()
  }

  @WorkerKey(TagExpirationCleanupWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory : WorkerInstanceFactory<TagExpirationCleanupWorker>
}
