package dev.msfjarvis.lobsters.data.local

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.msfjarvis.lobsters.database.LobstersDatabase

actual class DriverFactory(private val context: Context) {
  actual fun createDriver(): SqlDriver {
    return AndroidSqliteDriver(LobstersDatabase.Schema, context, LobstersDatabaseName)
  }
}
