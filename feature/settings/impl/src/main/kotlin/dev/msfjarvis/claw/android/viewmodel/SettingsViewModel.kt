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
import dev.msfjarvis.claw.feature.auth.api.AuthSessionStore
import dev.msfjarvis.claw.feature.auth.api.WebViewCookieStore
import dev.msfjarvis.claw.feature.posts.api.PostDataTransfer
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.msfjarvis.claw.feature.settings.impl.SettingsAppConfig
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class SettingsViewModel(
  private val authSessionStore: AuthSessionStore,
  private val webViewCookieStore: WebViewCookieStore,
  private val dailySavedPostReminderSettings: DailySavedPostReminderSettings,
  private val dailySavedPostReminderScheduler: DailySavedPostReminderScheduler,
  val appConfig: SettingsAppConfig,
  savedPosts: SavedPosts,
  private val postDataTransfer: PostDataTransfer,
) : ViewModel() {

  val isLoggedIn =
    authSessionStore
      .isLoggedIn()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

  val username =
    authSessionStore.username().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  val savedPostsCount =
    savedPosts.savedPostsCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

  val isDailySavedPostReminderEnabled = dailySavedPostReminderSettings.isEnabled
  val dailySavedPostReminderTime = dailySavedPostReminderSettings.reminderTime

  fun setDailySavedPostReminderEnabled(enabled: Boolean) {
    dailySavedPostReminderScheduler.setEnabled(enabled)
  }

  fun setDailySavedPostReminderTime(time: LocalTime) {
    viewModelScope.launch { dailySavedPostReminderScheduler.setReminderTime(time) }
  }

  fun logout() {
    authSessionStore.clear()
    webViewCookieStore.clearLobstersCookies()
  }

  fun saveCookie(cookie: String, username: String) {
    authSessionStore.set(cookie, username)
  }

  fun saveAnubisCookie(cookie: String) {
    authSessionStore.saveClearanceCookie(cookie)
  }

  suspend fun importPosts(input: InputStream): Result<Unit> = postDataTransfer.importPosts(input)

  suspend fun exportPostsAsJson(output: OutputStream) {
    postDataTransfer.exportPostsAsJson(output)
  }
}
