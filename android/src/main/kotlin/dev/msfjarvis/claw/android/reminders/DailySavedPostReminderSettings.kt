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

interface DailySavedPostReminderSettings {
  val isEnabled: StateFlow<Boolean>

  fun setEnabled(enabled: Boolean)
}

@Inject
@ContributesBinding(AppScope::class, binding = binding<DailySavedPostReminderSettings>())
@SingleIn(AppScope::class)
class SharedPreferencesDailySavedPostReminderSettings(context: Context) :
  DailySavedPostReminderSettings {

  private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
  private val enabled = MutableStateFlow(preferences.getBoolean(ENABLED_KEY, false))

  override val isEnabled: StateFlow<Boolean> = enabled

  override fun setEnabled(enabled: Boolean) {
    preferences.edit { putBoolean(ENABLED_KEY, enabled) }
    this.enabled.value = enabled
  }

  private companion object {
    const val PREFERENCES_NAME = "daily_saved_post_reminder"
    const val ENABLED_KEY = "enabled"
  }
}
