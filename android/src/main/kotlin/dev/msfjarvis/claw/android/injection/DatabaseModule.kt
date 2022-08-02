package dev.msfjarvis.claw.android.injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.createDatabase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  fun provideDatabase(@ApplicationContext context: Context): LobstersDatabase {
    return createDatabase(context)
  }
}
