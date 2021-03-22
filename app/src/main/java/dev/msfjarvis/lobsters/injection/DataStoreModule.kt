package dev.msfjarvis.lobsters.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class PreferenceStoreFileNameQualifier

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

  @Provides
  fun provideDataStore(
    @ApplicationContext context: Context,
    @PreferenceStoreFileNameQualifier fileName: String,
  ): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(fileName) }
  }

  @Provides
  @PreferenceStoreFileNameQualifier
  fun provideDataStoreFilename(): String {
    return "Claw_preferences"
  }
}
