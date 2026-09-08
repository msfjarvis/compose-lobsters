/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import org.junit.jupiter.api.Test

class DailySavedPostReminderSchedulerTest {

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
}
