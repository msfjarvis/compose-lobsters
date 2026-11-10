/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.tags.impl

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.msfjarvis.claw.core.work.api.WorkManagerRequests
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import java.util.concurrent.TimeUnit

@Inject
@ContributesIntoSet(AppScope::class)
class TagCleanupRequests(private val workManager: WorkManager) : WorkManagerRequests {
  override fun enqueue() {
    val request = PeriodicWorkRequestBuilder<TagExpirationCleanupWorker>(24, TimeUnit.HOURS).build()
    workManager.enqueueUniquePeriodicWork(
      "cleanupExpiredTags",
      ExistingPeriodicWorkPolicy.KEEP,
      request,
    )
  }
}
