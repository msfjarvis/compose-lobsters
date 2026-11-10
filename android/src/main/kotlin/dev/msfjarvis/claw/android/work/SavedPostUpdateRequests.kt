/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.msfjarvis.claw.core.work.api.WorkManagerRequests
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import java.util.concurrent.TimeUnit

@Inject
@ContributesIntoSet(AppScope::class)
class SavedPostUpdateRequests(private val workManager: WorkManager) : WorkManagerRequests {
  override fun enqueue() {
    val request =
      PeriodicWorkRequestBuilder<SavedPostUpdaterWorker>(24, TimeUnit.HOURS)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    workManager.enqueueUniquePeriodicWork(
      "updateSavedPosts",
      ExistingPeriodicWorkPolicy.KEEP,
      request,
    )
  }
}
