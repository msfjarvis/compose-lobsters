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
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

@OptIn(ExperimentalCoroutinesApi::class)
class TagBlockRepositoryTest {
  @TempDir lateinit var tempDir: File

  private lateinit var tagBlocksQueries: TagBlocksQueries
  private val testDispatcher = UnconfinedTestDispatcher()

  @BeforeEach
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    driver.execute(
      null,
      """
      CREATE TABLE IF NOT EXISTS TagBlocks (
        tag TEXT PRIMARY KEY NOT NULL,
        expiration_millis INTEGER
      )
      """
        .trimIndent(),
      0,
    )

    tagBlocksQueries = TagBlocksQueries(driver)
  }

  private fun createTestDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create { File(tempDir, "test_preferences.preferences_pb") }
  }

  @Test
  fun `saveTagBlock inserts new tag block successfully`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val expirationTime = System.currentTimeMillis() + 86400000
    repository.saveTagBlock("android", expirationTime)

    val tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("android")
  }

  @Test
  fun `saveTagBlock with null expiration creates permanent block`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    repository.saveTagBlock("kotlin", null)

    val tagBlocks = repository.getTagBlocks().first()
    assertThat(tagBlocks).hasSize(1)
    assertThat(tagBlocks[0].tag).isEqualTo("kotlin")
    assertThat(tagBlocks[0].isPermanent).isTrue()
  }

  @Test
  fun `saveTagBlock replaces existing tag block`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val firstExpiration = System.currentTimeMillis() + 86400000
    repository.saveTagBlock("rust", firstExpiration)

    val secondExpiration = System.currentTimeMillis() + 172800000
    repository.saveTagBlock("rust", secondExpiration)

    val tagBlocks = repository.getTagBlocks().first()
    assertThat(tagBlocks).hasSize(1)
    assertThat(tagBlocks[0].expirationMillis).isEqualTo(secondExpiration)
  }

  @Test
  fun `removeTagBlock deletes tag successfully`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    repository.saveTagBlock("python", null)
    repository.saveTagBlock("javascript", null)

    var tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("python", "javascript")

    repository.removeTagBlock("python")

    tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("javascript")
  }

  @Test
  fun `getSavedTags filters out expired tags`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

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
  fun `getTagBlocks returns all blocks including expired`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired", pastTime)
    repository.saveTagBlock("active", futureTime)
    repository.saveTagBlock("permanent", null)

    val tagBlocks = repository.getTagBlocks().first()

    assertThat(tagBlocks).hasSize(3)
    assertThat(tagBlocks.map { it.tag }).containsExactly("expired", "active", "permanent")
  }

  @Test
  fun `removeExpiredTags only removes expired blocks`() = runTest {
    val dataStore = createTestDataStore()
    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

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

  @Test
  fun `migrates legacy DataStore tags to SQLite`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("android", "kotlin", "java") }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val tags = repository.getSavedTags().first()

    assertThat(tags).containsExactly("android", "kotlin", "java")

    val tagBlocks = repository.getTagBlocks().first()
    assertThat(tagBlocks.all { it.isPermanent }).isTrue()
  }

  @Test
  fun `migrates JSON DataStore format to SQLite`() = runTest {
    val dataStore = createTestDataStore()
    val datastoreTagsKey = stringPreferencesKey("tags_with_expiration")

    val expirationTime = System.currentTimeMillis() + 86400000
    val tagMap = mapOf("rust" to expirationTime, "go" to null)

    dataStore.edit { prefs -> prefs[datastoreTagsKey] = Json.encodeToString(tagMap) }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("rust", "go")

    val tagBlocks = repository.getTagBlocks().first()
    val rustBlock = tagBlocks.find { it.tag == "rust" }
    val goBlock = tagBlocks.find { it.tag == "go" }

    assertThat(rustBlock?.expirationMillis).isEqualTo(expirationTime)
    assertThat(goBlock?.isPermanent).isTrue()
  }

  @Test
  fun `migration only runs once`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("security") }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    repository.getSavedTags().first()

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("privacy") }

    val tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("security")
    assertThat(tags).doesNotContain("privacy")
  }

  @Test
  fun `prefers JSON format over legacy format during migration`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")
    val datastoreTagsKey = stringPreferencesKey("tags_with_expiration")

    dataStore.edit { prefs ->
      prefs[legacyTagsKey] = setOf("old-tag")
      prefs[datastoreTagsKey] = Json.encodeToString(mapOf("new-tag" to null))
    }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)

    val tags = repository.getSavedTags().first()

    assertThat(tags).containsExactly("new-tag")
    assertThat(tags).doesNotContain("old-tag")
  }

  @Test
  fun `migration cleans up legacy DataStore after migrating`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")

    dataStore.edit { prefs -> prefs[legacyTagsKey] = setOf("android", "kotlin") }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)
    repository.getSavedTags().first()

    val prefs = dataStore.data.first()
    assertThat(prefs[legacyTagsKey]).isNull()
  }

  @Test
  fun `migration cleans up JSON DataStore after migrating`() = runTest {
    val dataStore = createTestDataStore()
    val datastoreTagsKey = stringPreferencesKey("tags_with_expiration")

    dataStore.edit { prefs -> prefs[datastoreTagsKey] = Json.encodeToString(mapOf("rust" to null)) }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)
    repository.getSavedTags().first()

    val prefs = dataStore.data.first()
    assertThat(prefs[datastoreTagsKey]).isNull()
  }

  @Test
  fun `migration cleans up both DataStore formats when both present`() = runTest {
    val dataStore = createTestDataStore()
    val legacyTagsKey = stringSetPreferencesKey("tags")
    val datastoreTagsKey = stringPreferencesKey("tags_with_expiration")

    dataStore.edit { prefs ->
      prefs[legacyTagsKey] = setOf("old-tag")
      prefs[datastoreTagsKey] = Json.encodeToString(mapOf("new-tag" to null))
    }

    val repository = TagBlockRepository(tagBlocksQueries, dataStore, testDispatcher, testDispatcher)
    repository.getSavedTags().first()

    val prefs = dataStore.data.first()
    assertThat(prefs[legacyTagsKey]).isNull()
    assertThat(prefs[datastoreTagsKey]).isNull()
  }
}
