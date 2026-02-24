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
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json

@Inject
class TagFilterRepository(private val preferences: DataStore<Preferences>) {
  private val legacyTagsKey = stringSetPreferencesKey("tags")
  private val tagsKey = stringPreferencesKey("tags_with_expiration")

  private suspend fun migrateIfNeeded() {
    preferences.edit { prefs ->
      val legacyTags = prefs[legacyTagsKey]
      val newKeyJson = prefs[tagsKey]

      if (legacyTags != null) {
        val existingTagMap =
          if (newKeyJson != null) {
            Json.decodeFromString<Map<String, Long?>>(newKeyJson)
          } else {
            emptyMap()
          }

        val migratedTags = legacyTags.associateWith { null as Long? }
        val mergedMap = existingTagMap + migratedTags

        prefs[tagsKey] = Json.encodeToString(mergedMap)
        prefs.remove(legacyTagsKey)
      }
    }
  }

  fun getSavedTags(): Flow<Set<String>> {
    return preferences.data
      .onStart { migrateIfNeeded() }
      .map { prefs ->
        val json = prefs[tagsKey] ?: return@map emptySet()
        val tagBlocks = Json.decodeFromString<Map<String, Long?>>(json)
        val now = System.currentTimeMillis()
        tagBlocks.filterValues { expiration -> expiration == null || expiration > now }.keys
      }
  }

  fun getTagBlocks(): Flow<List<TagBlock>> {
    return preferences.data
      .onStart { migrateIfNeeded() }
      .map { prefs ->
        val json = prefs[tagsKey] ?: return@map emptyList()
        val tagMap = Json.decodeFromString<Map<String, Long?>>(json)
        tagMap.map { (tag, expiration) -> TagBlock(tag, expiration) }
      }
  }

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    preferences.edit { prefs ->
      val json = prefs[tagsKey]
      val tagMap =
        if (json != null) {
          Json.decodeFromString<Map<String, Long?>>(json).toMutableMap()
        } else {
          mutableMapOf()
        }
      tagMap[tag] = expirationMillis
      prefs[tagsKey] = Json.encodeToString(tagMap)
    }
  }

  suspend fun removeTagBlock(tag: String) {
    preferences.edit { prefs ->
      val json = prefs[tagsKey] ?: return@edit
      val tagMap = Json.decodeFromString<Map<String, Long?>>(json).toMutableMap()
      tagMap.remove(tag)
      prefs[tagsKey] = Json.encodeToString(tagMap)
    }
  }

  suspend fun removeExpiredTags() {
    preferences.edit { prefs ->
      val json = prefs[tagsKey] ?: return@edit
      val tagMap = Json.decodeFromString<Map<String, Long?>>(json)
      val now = System.currentTimeMillis()
      val activeTagMap =
        tagMap.filterValues { expiration -> expiration == null || expiration > now }
      prefs[tagsKey] = Json.encodeToString(activeTagMap)
    }
  }
}
