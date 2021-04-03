package dev.msfjarvis.lobsters.data.local

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.msfjarvis.lobsters.database.LobstersDatabase

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    return driver
  }
}
