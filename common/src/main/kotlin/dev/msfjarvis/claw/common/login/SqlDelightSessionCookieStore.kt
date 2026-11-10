/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.login

import dev.msfjarvis.claw.core.database.SessionCookieStorage
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightSessionCookieStore(private val storage: SessionCookieStorage) : SessionCookieStore {
  override fun get(): String? = storage.getCookie()

  override fun getUsername(): String? = storage.getUsername()

  override fun set(cookie: String, username: String) {
    storage.set(cookie, username)
  }

  override fun clear() {
    storage.clear()
  }

  override fun isLoggedIn(): Flow<Boolean> = storage.isLoggedIn()

  override fun username(): Flow<String?> = storage.username()
}
