/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.CSVAdapter

fun setupDatabase(): LobstersDatabase {
  val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
  LobstersDatabase.Schema.create(driver)
  return LobstersDatabase(
    driver,
    PostComments.Adapter(CSVAdapter()),
    SavedPost.Adapter(IntColumnAdapter, CSVAdapter()),
  )
}
