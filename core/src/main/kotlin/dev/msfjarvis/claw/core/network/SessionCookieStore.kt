/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.network

import kotlinx.coroutines.flow.Flow

interface SessionCookieStore {
  fun get(): String?

  fun set(cookie: String)

  fun clear()

  fun isLoggedIn(): Flow<Boolean>
}
