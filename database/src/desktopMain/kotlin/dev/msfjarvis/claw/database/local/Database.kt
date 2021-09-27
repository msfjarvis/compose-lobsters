package dev.msfjarvis.claw.database.local

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
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
