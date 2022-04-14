package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import java.io.File

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    val env = System.getenv()
    val home = env["HOME"]
    val sqlitePath = "$home${File.pathSeparatorChar}.cache${File.pathSeparatorChar}Claw_database.db"
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$sqlitePath")
    LobstersDatabase.Schema.create(driver)
    return driver
  }
}
