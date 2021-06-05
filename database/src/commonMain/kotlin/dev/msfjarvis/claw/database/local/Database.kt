package dev.msfjarvis.claw.database.local

import com.squareup.sqldelight.db.SqlDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.TagsAdapter

internal const val LobstersDatabaseName = "SavedPosts.db"

expect class DriverFactory {
  fun createDriver(): SqlDriver
}

private fun getTagsAdapter() = TagsAdapter()

fun createDatabase(driverFactory: DriverFactory): LobstersDatabase {
  val driver = driverFactory.createDriver()
  return LobstersDatabase(driver, SavedPost.Adapter(getTagsAdapter()))
}
