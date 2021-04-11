package dev.msfjarvis.lobsters.injection

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.msfjarvis.lobsters.data.backup.BackupHandler
import dev.msfjarvis.lobsters.data.repo.LobstersRepository

@Module
@InstallIn(ActivityComponent::class)
object BackupModule {
  @Provides
  fun provideBackupHandler(
    repository: LobstersRepository,
    moshi: Moshi,
  ): BackupHandler {
    return BackupHandler(repository, moshi)
  }
}
