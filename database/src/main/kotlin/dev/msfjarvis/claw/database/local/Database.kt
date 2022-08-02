package dev.msfjarvis.claw.database.local

import android.content.Context
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.TagsAdapter

internal const val LobstersDatabaseName = "SavedPosts.db"

fun createDatabase(context: Context): LobstersDatabase {
  val driver = AndroidSqliteDriver(LobstersDatabase.Schema, context, LobstersDatabaseName)
  return LobstersDatabase(driver, SavedPost.Adapter(IntColumnAdapter, TagsAdapter()))
}
