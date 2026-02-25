/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.Inject
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Inject
class TagBlockRepository(
  private val tagBlocksQueries: TagBlocksQueries,
  private val preferences: DataStore<Preferences>,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  private val legacyTagsKey = stringSetPreferencesKey("tags")
  private val timedTagsKey = stringPreferencesKey("tags_with_expiration")

  private var migrationCompleted = false

  private suspend fun migrateFromDataStore() {
    if (migrationCompleted) return

    val prefs = preferences.data.first()
    val timedTags = prefs[timedTagsKey]
    val legacyTags = prefs[legacyTagsKey]

    if (timedTags == null && legacyTags == null) {
      migrationCompleted = true
      Sentry.metrics().count("tag_sqlite_migration_not_required")
      return
    } else {
      Sentry.metrics().count("tag_sqlite_migration_required")
    }

    withContext(writeDispatcher) {
      if (timedTags != null) {
        val tagMap = Json.decodeFromString<Map<String, Long?>>(timedTags)
        tagMap.forEach { (tag, expiration) -> tagBlocksQueries.insertOrReplace(tag, expiration) }
      } else {
        legacyTags?.forEach { tag -> tagBlocksQueries.insertOrReplace(tag, null) }
      }

      // Clean up DataStore after successful migration
      preferences.edit { prefs ->
        prefs.remove(timedTagsKey)
        prefs.remove(legacyTagsKey)
      }

      migrationCompleted = true
    }
  }

  fun getSavedTags(): Flow<Set<String>> {
    val now = System.currentTimeMillis()
    return tagBlocksQueries
      .selectActiveTags(now)
      .asFlow()
      .mapToList(readDispatcher)
      .onStart { migrateFromDataStore() }
      .map { it.toSet() }
  }

  fun getTagBlocks(): Flow<List<TagBlock>> {
    return tagBlocksQueries
      .selectAll()
      .asFlow()
      .mapToList(readDispatcher)
      .onStart { migrateFromDataStore() }
      .map { blocks -> blocks.map { TagBlock(it.tag, it.expiration_millis) } }
  }

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    withContext(writeDispatcher) { tagBlocksQueries.insertOrReplace(tag, expirationMillis) }
  }

  suspend fun removeTagBlock(tag: String) {
    withContext(writeDispatcher) { tagBlocksQueries.deleteByTag(tag) }
  }

  suspend fun removeExpiredTags() {
    val now = System.currentTimeMillis()
    withContext(writeDispatcher) { tagBlocksQueries.deleteExpired(now) }
  }
}
