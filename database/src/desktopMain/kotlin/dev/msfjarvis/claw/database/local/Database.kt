package dev.msfjarvis.claw.database.local

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    return driver
  }
}
