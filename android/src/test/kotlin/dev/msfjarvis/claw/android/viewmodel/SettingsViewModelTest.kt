/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.android.reminders.DEFAULT_DAILY_SAVED_POST_REMINDER_TIME
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderScheduler
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderSettings
import dev.msfjarvis.claw.core.network.SessionCookieStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

  @Test
  fun `logout clears persisted and webview sessions`() {
    val sessionCookieStore = FakeSessionCookieStore()
    val webViewCookieStore = FakeWebViewCookieStore()
    val viewModel =
      SettingsViewModel(
        sessionCookieStore,
        webViewCookieStore,
        FakeDailySavedPostReminderSettings(),
        FakeDailySavedPostReminderScheduler(),
      )

    viewModel.logout()

    assertThat(sessionCookieStore.cleared).isTrue()
    assertThat(webViewCookieStore.cleared).isTrue()
  }

  @Test
  fun `saveCookie persists username`() {
    val sessionCookieStore = FakeSessionCookieStore()
    val viewModel =
      SettingsViewModel(
        sessionCookieStore,
        FakeWebViewCookieStore(),
        FakeDailySavedPostReminderSettings(),
        FakeDailySavedPostReminderScheduler(),
      )

    viewModel.saveCookie("cookie=value", "alice")

    assertThat(sessionCookieStore.savedCookie).isEqualTo("cookie=value")
    assertThat(sessionCookieStore.savedUsername).isEqualTo("alice")
  }

  @Test
  fun `enabling reminder delegates scheduling`() {
    val scheduler = FakeDailySavedPostReminderScheduler()
    val viewModel =
      SettingsViewModel(
        FakeSessionCookieStore(),
        FakeWebViewCookieStore(),
        FakeDailySavedPostReminderSettings(),
        scheduler,
      )

    viewModel.setDailySavedPostReminderEnabled(true)

    assertThat(scheduler.lastEnabled).isTrue()
  }

  @Test
  fun `setting reminder time delegates scheduling`() {
    val scheduler = FakeDailySavedPostReminderScheduler()
    val viewModel =
      SettingsViewModel(
        FakeSessionCookieStore(),
        FakeWebViewCookieStore(),
        FakeDailySavedPostReminderSettings(),
        scheduler,
      )

    viewModel.setDailySavedPostReminderTime(LocalTime(13, 30))

    assertThat(scheduler.lastReminderTime).isEqualTo(LocalTime(13, 30))
  }

  private class FakeSessionCookieStore : SessionCookieStore {
    var cleared = false
    var savedCookie: String? = null
    var savedUsername: String? = null

    override fun get(): String? = savedCookie

    override fun getUsername(): String? = savedUsername

    override fun set(cookie: String, username: String) {
      savedCookie = cookie
      savedUsername = username
    }

    override fun clear() {
      cleared = true
      savedCookie = null
      savedUsername = null
    }

    override fun isLoggedIn(): Flow<Boolean> = flowOf(savedUsername != null)

    override fun username(): Flow<String?> = flowOf(savedUsername)
  }

  private class FakeDailySavedPostReminderSettings : DailySavedPostReminderSettings {
    private val enabled = MutableStateFlow(false)
    private val reminderTimeFlow = MutableStateFlow(DEFAULT_DAILY_SAVED_POST_REMINDER_TIME)

    override val isEnabled: StateFlow<Boolean> = enabled
    override val reminderTime: StateFlow<LocalTime> = reminderTimeFlow

    override fun setEnabled(enabled: Boolean) {
      this.enabled.value = enabled
    }

    override fun setReminderTime(time: LocalTime) {
      reminderTimeFlow.value = time
    }
  }

  private class FakeDailySavedPostReminderScheduler : DailySavedPostReminderScheduler {
    var lastEnabled = false
    var lastReminderTime: LocalTime? = null

    override fun setEnabled(enabled: Boolean) {
      lastEnabled = enabled
    }

    override fun setReminderTime(time: LocalTime) {
      lastReminderTime = time
    }

    override fun reconcile() = Unit

    override fun scheduleNext() = Unit
  }

  private class FakeWebViewCookieStore : WebViewCookieStore {
    var cleared = false

    override fun clearLobstersCookies() {
      cleared = true
    }
  }
}
