/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.core.network.SessionCookieStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Test

class SettingsViewModelTest {

  @Test
  fun `logout clears persisted and webview sessions`() {
    val sessionCookieStore = FakeSessionCookieStore()
    val webViewCookieStore = FakeWebViewCookieStore()
    val viewModel = SettingsViewModel(sessionCookieStore, webViewCookieStore)

    viewModel.logout()

    assertThat(sessionCookieStore.cleared).isTrue()
    assertThat(webViewCookieStore.cleared).isTrue()
  }

  @Test
  fun `saveCookie persists username`() {
    val sessionCookieStore = FakeSessionCookieStore()
    val viewModel = SettingsViewModel(sessionCookieStore, FakeWebViewCookieStore())

    viewModel.saveCookie("cookie=value", "alice")

    assertThat(sessionCookieStore.savedCookie).isEqualTo("cookie=value")
    assertThat(sessionCookieStore.savedUsername).isEqualTo("alice")
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

  private class FakeWebViewCookieStore : WebViewCookieStore {
    var cleared = false

    override fun clearLobstersCookies() {
      cleared = true
    }
  }
}
