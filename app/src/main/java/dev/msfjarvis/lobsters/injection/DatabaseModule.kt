package dev.msfjarvis.lobsters.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.local.DriverFactory
import dev.msfjarvis.lobsters.data.local.createDatabase
import dev.msfjarvis.lobsters.database.LobstersDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun providesDriverFactory(@ApplicationContext context: Context): DriverFactory {
    return DriverFactory(context)
  }

  @Provides
  @Singleton
  fun providesLobstersDatabase(driverFactory: DriverFactory): LobstersDatabase {
    return createDatabase(driverFactory)
  }
}
