/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

@OptIn(ExperimentalCoroutinesApi::class)
class TagFilterRepositoryTest {
  @TempDir lateinit var tempDir: File

  @BeforeEach
  fun setup() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
  }

  private fun createTestDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create { File(tempDir, "test_preferences.preferences_pb") }
  }

  @Test
  fun `migrates legacy tags to new format with null expiration`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("android", "kotlin", "java") }

    val repository = TagFilterRepository(dataStore)
    val tags = repository.getSavedTags().first()

    assertThat(tags).containsExactly("android", "kotlin", "java")

    val dataStoreSnapshot = dataStore.data.first()
    assertThat(dataStoreSnapshot[legacyTagsKey]).isNull()
  }

  @Test
  fun `migration preserves tags as permanent blocks`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("security", "privacy") }

    val repository = TagFilterRepository(dataStore)
    val tagBlocks = repository.getTagBlocks().first()

    assertThat(tagBlocks).hasSize(2)
    assertThat(tagBlocks.all { it.isPermanent }).isTrue()
    assertThat(tagBlocks.map { it.tag }).containsExactly("security", "privacy")
  }

  @Test
  fun `merges legacy tags with existing new format tags`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagFilterRepository(dataStore)

    val expirationTime = System.currentTimeMillis() + 86400000
    repository.saveTagBlock("rust", expirationTime)

    val legacyTagsKey = stringSetPreferencesKey("tags")
    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("python", "javascript") }

    val tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("rust", "python", "javascript")

    val dataStoreSnapshot = dataStore.data.first()
    assertThat(dataStoreSnapshot[legacyTagsKey]).isNull()
  }

  @Test
  fun `filters out expired tags from getSavedTags`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagFilterRepository(dataStore)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired-tag", pastTime)
    repository.saveTagBlock("active-tag", futureTime)
    repository.saveTagBlock("permanent-tag", null)

    val tags = repository.getSavedTags().first()

    assertThat(tags).containsExactly("active-tag", "permanent-tag")
    assertThat(tags).doesNotContain("expired-tag")
  }

  @Test
  fun `removeExpiredTags only removes expired blocks`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagFilterRepository(dataStore)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired1", pastTime)
    repository.saveTagBlock("expired2", pastTime - 5000)
    repository.saveTagBlock("future", futureTime)
    repository.saveTagBlock("permanent", null)

    repository.removeExpiredTags()

    val remaining = repository.getTagBlocks().first()
    assertThat(remaining).hasSize(2)
    assertThat(remaining.map { it.tag }).containsExactly("future", "permanent")
  }
}
