/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalTime

val DEFAULT_DAILY_SAVED_POST_REMINDER_TIME = LocalTime(9, 0)

interface DailySavedPostReminderSettings {
  val isEnabled: StateFlow<Boolean>
  val reminderTime: StateFlow<LocalTime>

  fun setEnabled(enabled: Boolean)

  fun setReminderTime(time: LocalTime)
}
