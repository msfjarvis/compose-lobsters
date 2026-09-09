/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.android.viewmodel.DailySavedPostNotificationRepository
import dev.msfjarvis.claw.database.local.DailySavedPostNotification
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Test

class DailySavedPostReminderSchedulerTest {

  @Test
  fun `changing the schedule deletes the current day delivery record`() = runTest {
    val settings = FakeSettings()
    val repository = FakeNotificationRepository()
    var schedules = 0

    updateSchedule(
      time = LocalTime(13, 30),
      settings = settings,
      notificationRepository = repository,
      clock = Clock.fixed(Instant.parse("2026-09-05T00:30:00Z")),
      timeZone = TimeZone.of("America/Los_Angeles"),
      scheduleNext = { schedules++ },
    )

    assertThat(settings.reminderTime.value).isEqualTo(LocalTime(13, 30))
    assertThat(repository.deletedDates).containsExactly("2026-09-04")
    assertThat(schedules).isEqualTo(1)
  }

  @Test
  fun `initial delay targets reminder time later today`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(9, 0),
        now = Instant.parse("2026-03-19T08:45:00Z"),
        timeZone = TimeZone.UTC,
      )

    assertThat(delay).isEqualTo(15.minutes)
  }

  @Test
  fun `initial delay targets tomorrow when reminder time has passed`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(9, 0),
        now = Instant.parse("2026-03-19T09:00:00Z"),
        timeZone = TimeZone.UTC,
      )

    assertThat(delay).isEqualTo(24.hours)
  }

  @Test
  fun `initial delay keeps the selected time across daylight saving time`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(9, 0),
        now = Instant.parse("2026-03-07T15:00:00Z"),
        timeZone = TimeZone.of("America/New_York"),
      )

    assertThat(delay).isEqualTo(22.hours)
  }

  @Test
  fun `initial delay targets the gap-adjusted time on daylight saving day`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(2, 30),
        now = Instant.parse("2026-03-08T06:00:00Z"),
        timeZone = TimeZone.of("America/New_York"),
      )

    assertThat(delay).isEqualTo(90.minutes)
  }

  @Test
  fun `initial delay targets the selected time after a daylight saving gap`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(2, 30),
        now = Instant.parse("2026-03-08T07:30:00Z"),
        timeZone = TimeZone.of("America/New_York"),
      )

    assertThat(delay).isEqualTo(23.hours)
  }

  @Test
  fun `initial delay targets the future daylight saving overlap`() {
    val delay =
      dailySavedPostReminderInitialDelay(
        reminderTime = LocalTime(1, 30),
        now = Instant.parse("2026-11-01T06:15:00Z"),
        timeZone = TimeZone.of("America/New_York"),
      )

    assertThat(delay).isEqualTo(15.minutes)
  }

  private class FakeSettings : DailySavedPostReminderSettings {
    override val isEnabled: StateFlow<Boolean> = MutableStateFlow(true)
    override val reminderTime: MutableStateFlow<LocalTime> =
      MutableStateFlow(DEFAULT_DAILY_SAVED_POST_REMINDER_TIME)

    override fun setEnabled(enabled: Boolean) = Unit

    override fun setReminderTime(time: LocalTime) {
      reminderTime.value = time
    }
  }

  private class FakeNotificationRepository : DailySavedPostNotificationRepository {
    val deletedDates = mutableListOf<String>()

    override suspend fun getOrCreateSelection(
      localDate: String,
      selectedAtEpochMillis: Long,
    ): DailySavedPostNotification? = null

    override suspend fun markDelivered(localDate: String, deliveredAtEpochMillis: Long) = Unit

    override suspend fun deleteDeliveryForDate(localDate: String) {
      deletedDates += localDate
    }
  }

  // Source - https://stackoverflow.com/a/75419837
  // Posted by Endzeit
  // Retrieved 2026-09-07, License - CC BY-SA 4.0
  private class FixedClock(private val fixedInstant: Instant) : Clock {
    override fun now(): Instant = fixedInstant
  }

  private fun Clock.Companion.fixed(fixedInstant: Instant): Clock = FixedClock(fixedInstant)
}
