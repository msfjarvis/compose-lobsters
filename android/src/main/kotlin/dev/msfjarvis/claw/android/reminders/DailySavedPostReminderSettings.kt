/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.reminders

import android.content.Context
import androidx.core.content.edit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalTime

val DEFAULT_DAILY_SAVED_POST_REMINDER_TIME = LocalTime(9, 0)

interface DailySavedPostReminderSettings {
  val isEnabled: StateFlow<Boolean>
  val reminderTime: StateFlow<LocalTime>

  fun setEnabled(enabled: Boolean)

  fun setReminderTime(time: LocalTime)
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostReminderSettings>())
@SingleIn(AppScope::class)
class SharedPreferencesDailySavedPostReminderSettings(context: Context) :
  DailySavedPostReminderSettings {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
  private val enabled = MutableStateFlow(preferences.getBoolean(ENABLED_KEY, false))
  private val time =
    MutableStateFlow(
      if (preferences.contains(TIME_HOUR_KEY)) {
        LocalTime(
          preferences
            .getInt(TIME_HOUR_KEY, DEFAULT_DAILY_SAVED_POST_REMINDER_TIME.hour)
            .coerceIn(0, HOURS_PER_DAY - 1),
          preferences
            .getInt(TIME_MINUTE_KEY, DEFAULT_DAILY_SAVED_POST_REMINDER_TIME.minute)
            .coerceIn(0, MINUTES_PER_HOUR - 1),
        )
      } else {
        LocalTime.fromSecondOfDay(
          preferences
            .getInt(TIME_SECONDS_KEY, DEFAULT_DAILY_SAVED_POST_REMINDER_TIME.hour * 60 * 60)
            .coerceIn(0, SECONDS_PER_DAY - 1)
        )
      }
    )

  override val isEnabled: StateFlow<Boolean> = enabled
  override val reminderTime: StateFlow<LocalTime> = time

  override fun setEnabled(enabled: Boolean) {
    preferences.edit { putBoolean(ENABLED_KEY, enabled) }
    this.enabled.value = enabled
  }

  override fun setReminderTime(time: LocalTime) {
    preferences.edit {
      putInt(TIME_HOUR_KEY, time.hour)
      putInt(TIME_MINUTE_KEY, time.minute)
    }
    this.time.value = time
  }

  private companion object {
    const val PREFERENCES_NAME = "daily_saved_post_reminder"
    const val ENABLED_KEY = "enabled"
    const val TIME_SECONDS_KEY = "time_seconds"
    const val TIME_HOUR_KEY = "time_hour"
    const val TIME_MINUTE_KEY = "time_minute"
    const val HOURS_PER_DAY = 24
    const val MINUTES_PER_HOUR = 60
    const val SECONDS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR
  }
}
