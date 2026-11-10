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
import dev.msfjarvis.claw.feature.auth.api.AuthSessionStore
import dev.msfjarvis.claw.feature.auth.api.WebViewCookieStore
import dev.msfjarvis.claw.feature.posts.api.PostDataTransfer
import dev.msfjarvis.claw.feature.posts.api.SavedPostReference
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.msfjarvis.claw.feature.settings.impl.SettingsAppConfig
import dev.msfjarvis.claw.model.UIPost
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.Test

class SettingsViewModelTest {
  @Test
  fun `logout clears persisted and webview sessions`() {
    val auth = FakeAuthSessionStore()
    val webView = FakeWebViewCookieStore()

    viewModel(authSessionStore = auth, webViewCookieStore = webView).logout()

    assertThat(auth.cleared).isTrue()
    assertThat(webView.cleared).isTrue()
  }

  @Test
  fun `saveCookie persists username`() {
    val auth = FakeAuthSessionStore()

    viewModel(authSessionStore = auth).saveCookie("session=value", "alice")

    assertThat(auth.savedCookie).isEqualTo("session=value")
    assertThat(auth.savedUsername).isEqualTo("alice")
  }

  @Test
  fun `saving clearance preserves existing login cookies and username`() {
    val auth = FakeAuthSessionStore()
    auth.set("lobsters_trap=session; other=value", "alice")

    viewModel(authSessionStore = auth).saveAnubisCookie("clearance=value")

    assertThat(auth.savedCookie).contains("lobsters_trap=session")
    assertThat(auth.savedCookie).contains("other=value")
    assertThat(auth.savedCookie).contains("clearance=value")
    assertThat(auth.savedUsername).isEqualTo("alice")
  }

  @Test
  fun `saving anonymous clearance does not create a logged-in username`() {
    val auth = FakeAuthSessionStore()

    viewModel(authSessionStore = auth).saveAnubisCookie("clearance=value")

    assertThat(auth.savedCookie).contains("clearance=value")
    assertThat(auth.savedUsername).isNull()
  }

  @Test
  fun `enabling reminder delegates scheduling`() {
    val scheduler = FakeReminderScheduler()

    viewModel(reminderScheduler = scheduler).setDailySavedPostReminderEnabled(true)

    assertThat(scheduler.lastEnabled).isTrue()
  }

  @Test
  @OptIn(ExperimentalCoroutinesApi::class)
  fun `setting reminder time delegates scheduling`() = runTest {
    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
    try {
      val scheduler = FakeReminderScheduler()
      viewModel(reminderScheduler = scheduler).setDailySavedPostReminderTime(LocalTime(13, 30))
      advanceUntilIdle()
      assertThat(scheduler.time).isEqualTo(LocalTime(13, 30))
    } finally {
      Dispatchers.resetMain()
    }
  }

  private fun viewModel(
    authSessionStore: FakeAuthSessionStore = FakeAuthSessionStore(),
    webViewCookieStore: FakeWebViewCookieStore = FakeWebViewCookieStore(),
    reminderSettings: FakeReminderSettings = FakeReminderSettings(),
    reminderScheduler: FakeReminderScheduler = FakeReminderScheduler(),
    savedPosts: FakeSavedPosts = FakeSavedPosts(),
    postDataTransfer: FakePostDataTransfer = FakePostDataTransfer(),
  ) =
    SettingsViewModel(
      authSessionStore,
      webViewCookieStore,
      reminderSettings,
      reminderScheduler,
      SettingsAppConfig("test-version", 1) {},
      savedPosts,
      postDataTransfer,
    )

  private class FakeAuthSessionStore : AuthSessionStore {
    var cleared = false
    var savedCookie: String? = null
    var savedUsername: String? = null

    override fun set(cookie: String, username: String) {
      savedCookie = cookie
      savedUsername = username
    }

    override fun saveClearanceCookie(cookie: String) {
      savedCookie = listOfNotNull(savedCookie, cookie).joinToString("; ")
    }

    override fun clear() {
      cleared = true
    }

    override fun isLoggedIn() = kotlinx.coroutines.flow.flowOf(savedUsername != null)

    override fun username() = kotlinx.coroutines.flow.flowOf(savedUsername)
  }

  private class FakeWebViewCookieStore : WebViewCookieStore {
    var cleared = false

    override fun clearLobstersCookies() {
      cleared = true
    }
  }

  private class FakeReminderSettings : DailySavedPostReminderSettings {
    override val isEnabled: StateFlow<Boolean> = MutableStateFlow(false)
    override val reminderTime: StateFlow<LocalTime> =
      MutableStateFlow(DEFAULT_DAILY_SAVED_POST_REMINDER_TIME)

    override fun setEnabled(enabled: Boolean) = Unit

    override fun setReminderTime(time: LocalTime) = Unit
  }

  private class FakeReminderScheduler : DailySavedPostReminderScheduler {
    var lastEnabled = false
    var time: LocalTime? = null

    override fun setEnabled(enabled: Boolean) {
      lastEnabled = enabled
    }

    override suspend fun setReminderTime(time: LocalTime) {
      this.time = time
    }

    override fun reconcile() = Unit

    override fun scheduleNext() = Unit
  }

  private class FakePostDataTransfer : PostDataTransfer {
    override suspend fun importPosts(input: InputStream): Result<Unit> = Result.success(Unit)

    override suspend fun exportPostsAsJson(output: OutputStream) = Unit
  }

  private class FakeSavedPosts : SavedPosts {
    override val savedPostsSortedByDate = MutableStateFlow(emptyList<UIPost>())
    override val savedPostsCount = MutableStateFlow(4L)
    override val savedPostIds = MutableStateFlow(emptyList<String>())

    override suspend fun getPostIdsFromLastNDays(days: Long): List<SavedPostReference> = emptyList()

    override suspend fun getRecentPosts(limit: Long): List<UIPost> = emptyList()

    override suspend fun toggleSave(post: UIPost) = Unit

    override suspend fun savePosts(posts: List<UIPost>) = Unit

    override suspend fun importSerializedPosts(serialized: String) = Unit

    override suspend fun exportSerializedPosts(): String = ""
  }
}
