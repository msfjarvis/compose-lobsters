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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class RemoteSettingsTest {
  @get:Rule val tmpFolder = TemporaryFolder()

  private lateinit var dataStore: DataStore<Preferences>
  private lateinit var remoteSettings: RemoteSettings

  @Before
  fun setup() {
    dataStore =
      PreferenceDataStoreFactory.create {
        File(tmpFolder.newFolder(), "test_preferences.preferences_pb")
      }
    remoteSettings = RemoteSettings(dataStore)
  }

  @Test
  fun `getValue returns null when preference not set`() = runTest {
    val key = stringPreferencesKey("nonexistent_key")

    val result = remoteSettings.getValue(key)

    assertEquals(null, result)
  }

  @Test
  fun `getSearchSort returns default when not set`() = runTest {
    val result = remoteSettings.getSearchSort().first()

    assertEquals(RemoteSettings.DEFAULT_SEARCH_SORT, result)
  }

  @Test
  fun `getSearchSort returns saved value when set`() = runTest {
    val expectedSort = "oldest"

    remoteSettings.setSearchSort(expectedSort)
    val result = remoteSettings.getSearchSort().first()

    assertEquals(expectedSort, result)
  }
}
