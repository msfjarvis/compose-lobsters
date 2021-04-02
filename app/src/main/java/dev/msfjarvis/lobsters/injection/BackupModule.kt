package dev.msfjarvis.lobsters.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.data.backup.BackupHandler
import dev.msfjarvis.lobsters.database.LobstersDatabase

@Module
@InstallIn(ActivityComponent::class)
object BackupModule {
  @Provides
  fun provideBackupHandler(
    database: LobstersDatabase,
    moshi: Moshi,
  ): BackupHandler {
    return BackupHandler(database, moshi)
  }
}
