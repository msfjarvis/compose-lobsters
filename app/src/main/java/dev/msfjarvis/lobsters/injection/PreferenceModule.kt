package dev.msfjarvis.lobsters.injection

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.msfjarvis.lobsters.data.preferences.ClawPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {

  @Provides
  @Singleton
  fun provideClawPreferences(
    dataStore: DataStore<Preferences>,
  ): ClawPreferences {
    return ClawPreferences(dataStore)
  }
}
