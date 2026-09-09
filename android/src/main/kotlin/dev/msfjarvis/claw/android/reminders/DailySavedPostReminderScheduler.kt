/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.msfjarvis.claw.android.viewmodel.DailySavedPostNotificationRepository
import dev.msfjarvis.claw.android.work.DailySavedPostReminderWorker
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import java.util.concurrent.TimeUnit
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.offsetIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

interface DailySavedPostReminderScheduler {
  fun setEnabled(enabled: Boolean)

  suspend fun setReminderTime(time: LocalTime)

  fun reconcile()

  fun scheduleNext()
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostReminderScheduler>())
class WorkManagerDailySavedPostReminderScheduler(
  private val workManager: WorkManager,
  private val settings: DailySavedPostReminderSettings,
  private val notificationRepository: DailySavedPostNotificationRepository,
  private val clock: Clock,
  private val timeZone: TimeZone,
) : DailySavedPostReminderScheduler {
  override fun setEnabled(enabled: Boolean) {
    settings.setEnabled(enabled)
    scheduleNext()
  }

  override suspend fun setReminderTime(time: LocalTime) {
    updateSchedule(
      time = time,
      settings = settings,
      notificationRepository = notificationRepository,
      clock = clock,
      timeZone = timeZone,
      scheduleNext = ::scheduleNext,
    )
  }

  override fun reconcile() {
    workManager.cancelUniqueWork(LEGACY_WORK_NAME)
    schedule(ExistingWorkPolicy.KEEP)
  }

  override fun scheduleNext() {
    schedule(ExistingWorkPolicy.REPLACE)
  }

  private fun schedule(policy: ExistingWorkPolicy) {
    if (settings.isEnabled.value) {
      val workRequest =
        OneTimeWorkRequestBuilder<DailySavedPostReminderWorker>()
          .setInitialDelay(
            dailySavedPostReminderInitialDelay(settings.reminderTime.value).inWholeMilliseconds,
            TimeUnit.MILLISECONDS,
          )
          .build()
      workManager.enqueueUniqueWork(WORK_NAME, policy, workRequest)
    } else {
      workManager.cancelUniqueWork(WORK_NAME)
    }
  }

  companion object {
    const val WORK_NAME = "dailySavedPostReminderV2"
    const val LEGACY_WORK_NAME = "dailySavedPostReminder"
  }
}

internal suspend fun updateSchedule(
  time: LocalTime,
  settings: DailySavedPostReminderSettings,
  notificationRepository: DailySavedPostNotificationRepository,
  clock: Clock,
  timeZone: TimeZone,
  scheduleNext: () -> Unit,
) {
  settings.setReminderTime(time)
  notificationRepository.deleteDeliveryForDate(
    clock.now().toLocalDateTime(timeZone).date.toString()
  )
  scheduleNext()
}

internal fun dailySavedPostReminderInitialDelay(
  reminderTime: LocalTime,
  now: Instant = Clock.System.now(),
  timeZone: TimeZone = TimeZone.currentSystemDefault(),
): Duration {
  val localNow = now.toLocalDateTime(timeZone)
  val nextDate = localNow.date.plus(DatePeriod(days = 1))
  val reminderDate = if (localNow.time < reminderTime) localNow.date else nextDate
  val reminderDateTime = reminderDate.atTime(reminderTime)
  val nextReminder = reminderDateTime.toInstant(timeZone)
  val overlapReminder =
    nextReminder +
      (nextReminder.offsetIn(timeZone).totalSeconds - now.offsetIn(timeZone).totalSeconds).seconds
  val futureReminder =
    if (nextReminder > now) {
      nextReminder
    } else if (
      overlapReminder > now && overlapReminder.toLocalDateTime(timeZone) == reminderDateTime
    ) {
      overlapReminder
    } else {
      nextDate.atTime(reminderTime).toInstant(timeZone)
    }
  return futureReminder - now
}
