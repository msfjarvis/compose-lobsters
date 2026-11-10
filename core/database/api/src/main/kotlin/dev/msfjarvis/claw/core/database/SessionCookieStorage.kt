/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import kotlinx.coroutines.flow.Flow

/** Persistence operations for the current authenticated session. */
interface SessionCookieStorage {
  fun getCookie(): String?

  fun getUsername(): String?

  fun set(cookie: String, username: String)

  fun clear()

  fun isLoggedIn(): Flow<Boolean>

  fun username(): Flow<String?>
}
