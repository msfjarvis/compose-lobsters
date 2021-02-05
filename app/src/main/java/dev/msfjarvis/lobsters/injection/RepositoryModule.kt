package dev.msfjarvis.lobsters.injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.repo.LobstersRepository
import dev.msfjarvis.lobsters.database.LobstersDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun provideLobstersRepository(
    lobstersApi: LobstersApi,
    lobstersDatabase: LobstersDatabase
  ): LobstersRepository {
    return LobstersRepository(lobstersApi, lobstersDatabase)
  }
}
