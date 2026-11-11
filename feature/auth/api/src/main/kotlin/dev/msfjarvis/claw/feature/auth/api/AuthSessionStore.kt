/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.auth.api

import kotlinx.coroutines.flow.Flow

/**
 * Public authentication state and actions, independent of its persistence and transport adapters.
 */
interface AuthSessionStore {
  fun set(cookie: String, username: String)

  /** Persist a clearance cookie without changing the current account identity. */
  fun saveClearanceCookie(cookie: String)

  fun clear()

  fun isLoggedIn(): Flow<Boolean>

  fun username(): Flow<String?>
}
