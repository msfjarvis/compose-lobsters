/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.login

import dev.msfjarvis.claw.core.database.SessionCookieStorage
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.msfjarvis.claw.feature.auth.api.AuthSessionStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow
import okhttp3.Cookie
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl

private const val COOKIE_SEPARATOR = "\n"
private const val LOBSTERS_URL = "https://lobste.rs/"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, binding = binding<SessionCookieStore>())
@ContributesBinding(AppScope::class, binding = binding<AuthSessionStore>())
class SqlDelightSessionCookieStore(private val storage: SessionCookieStorage) :
  SessionCookieStore, AuthSessionStore {
  override fun get(): String? = storage.getCookie()

  override fun getUsername(): String? = storage.getUsername()

  override fun set(cookie: String, username: String) {
    storage.set(cookie, username)
  }

  override fun saveClearanceCookie(cookie: String) {
    val url = LOBSTERS_URL.toHttpUrl()
    if (Cookie.parse(url, cookie) == null) return
    val headers =
      Headers.Builder()
        .apply {
          storage
            .getCookie()
            ?.split(COOKIE_SEPARATOR)
            ?.flatMap { it.split("; ") }
            ?.filter(String::isNotBlank)
            ?.forEach { add("Set-Cookie", it) }
          add("Set-Cookie", cookie)
        }
        .build()
    val merged = LinkedHashMap<String, Cookie>()
    Cookie.parseAll(url, headers).forEach { merged[it.name] = it }
    val active = merged.values.filterNot { it.expiresAt <= System.currentTimeMillis() }
    storage.setCookie(active.joinToString(COOKIE_SEPARATOR) { "${it.name}=${it.value}" })
  }

  override fun clear() {
    storage.clear()
  }

  override fun isLoggedIn(): Flow<Boolean> = storage.isLoggedIn()

  override fun username(): Flow<String?> = storage.username()
}
