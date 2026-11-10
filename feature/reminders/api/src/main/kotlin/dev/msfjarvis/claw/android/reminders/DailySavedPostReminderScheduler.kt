/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import kotlinx.datetime.LocalTime

interface DailySavedPostReminderScheduler {
  fun setEnabled(enabled: Boolean)

  suspend fun setReminderTime(time: LocalTime)

  fun reconcile()

  fun scheduleNext()
}
