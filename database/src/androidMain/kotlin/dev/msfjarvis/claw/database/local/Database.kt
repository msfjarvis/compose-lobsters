package dev.msfjarvis.claw.database.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(LobstersDatabase.Schema, context, LobstersDatabaseName)
  }
}
