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
import androidx.datastore.preferences.core.stringSetPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagFilterRepository @Inject constructor(private val preferences: DataStore<Preferences>) {
  private val tagsKey = stringSetPreferencesKey("tags")

  fun getSavedTags(): Flow<Set<String>> {
    return preferences.data.map { prefs -> prefs[tagsKey] ?: emptySet() }
  }

  suspend fun saveTags(tags: Set<String>) {
    preferences.edit { prefs -> prefs[tagsKey] = tags }
  }
}
