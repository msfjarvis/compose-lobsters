package dev.msfjarvis.lobsters.data.local

import com.squareup.sqldelight.db.SqlDriver
import dev.msfjarvis.lobsters.data.model.TagsAdapter
import dev.msfjarvis.lobsters.database.LobstersDatabase

internal const val LobstersDatabaseName = "SavedPosts.db"

expect class DriverFactory {
  fun createDriver(): SqlDriver
}

private fun getTagsAdapter() = TagsAdapter()

fun createDatabase(driverFactory: DriverFactory): LobstersDatabase {
  val driver = driverFactory.createDriver()
  return LobstersDatabase(driver, SavedPost.Adapter(getTagsAdapter()))
}
