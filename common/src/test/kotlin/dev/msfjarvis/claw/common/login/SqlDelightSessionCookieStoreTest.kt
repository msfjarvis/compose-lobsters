/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.login

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.SessionCookieQueries
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SqlDelightSessionCookieStoreTest {
  private val dispatcher = StandardTestDispatcher()
  private lateinit var driver: JdbcSqliteDriver
  private lateinit var queries: SessionCookieQueries

  @BeforeEach
  fun setUp() {
    driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    queries = SessionCookieQueries(driver)
  }

  @AfterEach
  fun tearDown() {
    driver.close()
  }

  @Test
  fun `blank username is logged out`() =
    runTest(dispatcher) {
      queries.upsert("cookie", "")
      val store = SqlDelightSessionCookieStore(queries, dispatcher)

      assertThat(store.getUsername()).isNull()
      assertThat(store.username().first()).isNull()
      assertThat(store.isLoggedIn().first()).isFalse()
    }
}
