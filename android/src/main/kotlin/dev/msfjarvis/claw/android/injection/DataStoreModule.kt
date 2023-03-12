package dev.msfjarvis.claw.android.injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.msfjarvis.claw.util.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@ContributesTo(ApplicationScope::class)
interface DataStoreModule {

  @Singleton
  @Provides
  fun provideLoginDatastore(
    @ForScope(ApplicationScope::class) context: Context,
    dispatcherProvider: DispatcherProvider,
  ): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
      corruptionHandler = null,
      migrations = emptyList(),
      scope = CoroutineScope(dispatcherProvider.io() + SupervisorJob()),
    ) {
      context.preferencesDataStoreFile("login")
    }
  }
}
