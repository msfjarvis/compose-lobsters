package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.TagsAdapter

internal const val LobstersDatabaseName = "SavedPosts.db"

expect class DriverFactory {
  fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): LobstersDatabase {
  val driver = driverFactory.createDriver()
  return LobstersDatabase(driver, SavedPost.Adapter(IntColumnAdapter, TagsAdapter()))
}
