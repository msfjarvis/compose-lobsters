/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.smoketests

import dev.msfjarvis.claw.core.network.OkHttpClientConfigurator
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.logging.HttpLoggingInterceptor

@BindingContainer
@ContributesTo(AppScope::class)
object SmokeTestsBindings {
  @Provides
  fun provideCacheDir(): File =
    File(System.getProperty("java.io.tmpdir"), "claw-smoke-tests").apply {
      mkdirs()
    }

  @Provides fun provideSessionCookieStore(): SessionCookieStore = InMemorySessionCookieStore

  @Provides fun provideOkHttpClientConfigurator(): Set<OkHttpClientConfigurator> = emptySet()

  @Provides
  fun provideLogger(): HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger { _ -> }
}

private object InMemorySessionCookieStore : SessionCookieStore {
  private var cookie: String? = null
  private var usernameValue: String? = null
  private val loggedIn = MutableStateFlow(false)
  private val usernameFlow = MutableStateFlow<String?>(null)

  override fun get(): String? = cookie

  override fun getUsername(): String? = usernameValue

  override fun set(cookie: String, username: String) {
    this.cookie = cookie
    usernameValue = username
    loggedIn.value = true
    usernameFlow.value = username
  }

  override fun clear() {
    cookie = null
    usernameValue = null
    loggedIn.value = false
    usernameFlow.value = null
  }

  override fun isLoggedIn(): Flow<Boolean> = loggedIn

  override fun username(): Flow<String?> = usernameFlow
}
