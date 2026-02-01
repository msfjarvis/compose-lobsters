/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RemoteSettingsTest {
  @get:Rule val tmpFolder = TemporaryFolder()

  private fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create {
      File(tmpFolder.newFolder(), "test_preferences.preferences_pb")
    }
  }

  @Test
  fun `getValue returns null when preference not set`() = runTest {
    val dataStore = createDataStore()
    val remoteSettings = RemoteSettings(dataStore)
    val key = stringPreferencesKey("nonexistent_key")

    val result = remoteSettings.getValue(key)

    assertEquals(null, result)
  }

  @Test
  fun `getSearchSort returns default when not set`() = runTest {
    val dataStore = createDataStore()
    val remoteSettings = RemoteSettings(dataStore)

    val result = remoteSettings.getSearchSort().first()

    assertEquals(RemoteSettings.DEFAULT_SEARCH_SORT, result)
  }

  @Test
  fun `getSearchSort returns saved value when set`() = runTest {
    val dataStore = createDataStore()
    val remoteSettings = RemoteSettings(dataStore)
    val expectedSort = "oldest"

    remoteSettings.setSearchSort(expectedSort)
    val result = remoteSettings.getSearchSort().first()

    assertEquals(expectedSort, result)
  }

  @Test
  fun `getValue handles null gracefully`() = runTest {
    val dataStore = createDataStore()
    val remoteSettings = RemoteSettings(dataStore)
    val key = stringPreferencesKey("test_key")

    // Should not throw NPE when value is null
    val result = remoteSettings.getValue(key)

    assertEquals(null, result)
  }
}
