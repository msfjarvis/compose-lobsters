/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.msfjarvis.claw.android.work.DailySavedPostReminderWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import java.util.concurrent.TimeUnit

interface DailySavedPostReminderScheduler {
  fun setEnabled(enabled: Boolean)

  fun reconcile()
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostReminderScheduler>())
class WorkManagerDailySavedPostReminderScheduler(
  private val workManager: WorkManager,
  private val settings: DailySavedPostReminderSettings,
) : DailySavedPostReminderScheduler {
  override fun setEnabled(enabled: Boolean) {
    settings.setEnabled(enabled)
    reconcile()
  }

  override fun reconcile() {
    if (settings.isEnabled.value) {
      val workRequest =
        PeriodicWorkRequestBuilder<DailySavedPostReminderWorker>(24, TimeUnit.HOURS).build()
      workManager.enqueueUniquePeriodicWork(
        WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest,
      )
    } else {
      workManager.cancelUniqueWork(WORK_NAME)
    }
  }

  companion object {
    const val WORK_NAME = "dailySavedPostReminder"
  }
}
