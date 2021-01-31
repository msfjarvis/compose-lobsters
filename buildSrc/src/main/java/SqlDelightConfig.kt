import com.squareup.sqldelight.gradle.SqlDelightExtension

internal fun SqlDelightExtension.configureLobstersDatabase() {
  database("LobstersDatabase") {
    packageName = "dev.msfjarvis.lobsters.database"
    sourceFolders = listOf("sqldelight")
  }
}
