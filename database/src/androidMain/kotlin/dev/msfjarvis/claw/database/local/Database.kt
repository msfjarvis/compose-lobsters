package dev.msfjarvis.claw.database.local

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.msfjarvis.claw.database.LobstersDatabase

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(LobstersDatabase.Schema, context, LobstersDatabaseName)
  }
}
