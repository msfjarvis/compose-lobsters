/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.login

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.network.SessionCookieStore
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
class SqlDelightSessionCookieStore(
  private val queries: SessionCookieQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
) : SessionCookieStore {

  override fun get(): String? {
    return queries.get().executeAsOneOrNull()
  }

  override fun set(cookie: String) {
    queries.upsert(cookie)
  }

  override fun clear() {
    queries.deleteAll()
  }

  override fun isLoggedIn(): Flow<Boolean> {
    return queries.get().asFlow().mapToOneOrNull(readDispatcher).map { it != null }
  }
}
