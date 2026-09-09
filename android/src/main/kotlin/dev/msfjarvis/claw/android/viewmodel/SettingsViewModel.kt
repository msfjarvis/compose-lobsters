/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderScheduler
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderSettings
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class SettingsViewModel(
  private val sessionCookieStore: SessionCookieStore,
  private val webViewCookieStore: WebViewCookieStore,
  private val dailySavedPostReminderSettings: DailySavedPostReminderSettings,
  private val dailySavedPostReminderScheduler: DailySavedPostReminderScheduler,
) : ViewModel() {

  val isLoggedIn =
    sessionCookieStore
      .isLoggedIn()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
      )

  val username =
    sessionCookieStore
      .username()
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
      )

  val isDailySavedPostReminderEnabled = dailySavedPostReminderSettings.isEnabled
  val dailySavedPostReminderTime = dailySavedPostReminderSettings.reminderTime

  fun setDailySavedPostReminderEnabled(enabled: Boolean) {
    dailySavedPostReminderScheduler.setEnabled(enabled)
  }

  fun setDailySavedPostReminderTime(time: LocalTime) {
    viewModelScope.launch { dailySavedPostReminderScheduler.setReminderTime(time) }
  }

  fun logout() {
    sessionCookieStore.clear()
    webViewCookieStore.clearLobstersCookies()
  }

  fun saveCookie(cookie: String, username: String) {
    sessionCookieStore.set(cookie, username)
  }
}
