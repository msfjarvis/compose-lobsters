/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import androidx.work.ListenableWorker
import dev.msfjarvis.claw.android.viewmodel.DailySavedPostNotificationRepository
import dev.zacsweers.metro.Inject
import java.util.concurrent.CancellationException
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Executes the persisted daily reminder state machine independently of WorkManager so that the
 * logic can be tested in isolation.
 */
@Inject
class DailySavedPostReminderDelivery(
  private val repository: DailySavedPostNotificationRepository,
  private val settings: DailySavedPostReminderSettings,
  private val notifier: DailySavedPostReminderNotifier,
  private val clock: Clock,
  private val timeZone: TimeZone,
) {
  suspend fun deliver(): ListenableWorker.Result {
    val selectionInstant = clock.now()
    val localDate = selectionInstant.toLocalDateTime(timeZone).date.toString()

    if (!settings.isEnabled.value || !notifier.isAvailable()) {
      return ListenableWorker.Result.success()
    }

    val selection =
      try {
        repository.getOrCreateSelection(localDate, selectionInstant.toEpochMilliseconds())
      } catch (exception: Exception) {
        if (exception is CancellationException) throw exception
        return ListenableWorker.Result.retry()
      } ?: return ListenableWorker.Result.success()

    if (selection.deliveredAtEpochMillis != null) return ListenableWorker.Result.success()

    when (notifier.notify(localDate, selection)) {
      Unavailable -> return ListenableWorker.Result.success()
      Retry -> return ListenableWorker.Result.retry()
      Delivered -> Unit
    }

    return try {
      repository.markDelivered(localDate, clock.now().toEpochMilliseconds())
      ListenableWorker.Result.success()
    } catch (exception: Exception) {
      if (exception is CancellationException) throw exception
      ListenableWorker.Result.retry()
    }
  }
}
