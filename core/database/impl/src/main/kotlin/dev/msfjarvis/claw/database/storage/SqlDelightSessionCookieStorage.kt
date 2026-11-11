/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.database.SessionCookieStorage
import dev.msfjarvis.claw.database.local.SessionCookieQueries
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightSessionCookieStorage(
  private val queries: SessionCookieQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
) : SessionCookieStorage {
  override fun getCookie(): String? = queries.get().executeAsOneOrNull()

  override fun getUsername(): String? =
    queries.getUsername().executeAsOneOrNull()?.username?.takeIf(String::isNotBlank)

  override fun set(cookie: String, username: String) {
    queries.upsert(cookie, username)
  }

  override fun setCookie(cookie: String) {
    queries.setCookie(cookie)
  }

  override fun clear() {
    queries.deleteAll()
  }

  override fun isLoggedIn(): Flow<Boolean> =
    queries.getUsername().asFlow().mapToOneOrNull(readDispatcher).map {
      !it?.username.isNullOrBlank()
    }

  override fun username(): Flow<String?> =
    queries.getUsername().asFlow().mapToOneOrNull(readDispatcher).map {
      it?.username?.takeIf(String::isNotBlank)
    }
}
