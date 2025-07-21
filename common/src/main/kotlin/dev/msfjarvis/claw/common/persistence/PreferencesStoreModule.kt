/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.persistence

import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.zacsweers.metro.ForScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Module
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.multibindings.IntoSet
import io.github.aakira.napier.Napier

@Module
@ContributesTo(AppScope::class)
object PreferencesStoreModule {
  @Provides
  fun providePreferencesDataStore(
    @ForScope(AppScope::class) context: Context,
    migrations: Set<@JvmSuppressWildcards DataMigration<@JvmSuppressWildcards Preferences>>,
  ): DataStore<@JvmSuppressWildcards Preferences> {
    return DataStoreFactory.create(
      corruptionHandler =
        ReplaceFileCorruptionHandler {
          Napier.d(throwable = it) {
            "Preferences data store corruption detected, returning empty preferences."
          }
          emptyPreferences()
        },
      migrations = migrations.toList(),
      produceFile = { context.preferencesDataStoreFile("claw_preferences") },
      serializer = PreferencesFileSerializer,
    )
  }

  @Provides
  @IntoSet
  fun firstMigration(): DataMigration<Preferences> =
    object : DataMigration<Preferences> {
      override suspend fun shouldMigrate(currentData: Preferences): Boolean = false

      override suspend fun migrate(currentData: Preferences): Preferences = currentData

      override suspend fun cleanUp() {}
    }
}
