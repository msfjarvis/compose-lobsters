/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
class RemoteSettings(private val preferences: DataStore<Preferences>) {
  private val searchSortKey = stringPreferencesKey("search_sort")

  fun getSearchSort(): Flow<String> {
    return preferences.data.map { prefs -> prefs[searchSortKey] ?: DEFAULT_SEARCH_SORT }
  }

  suspend fun setSearchSort(sort: String) {
    preferences.edit { prefs -> prefs[searchSortKey] = sort }
  }

  /**
   * Get a value synchronously from preferences. This method should be used carefully as it can
   * return null if the preference is not set. Use this method only when you need a synchronous
   * value and have a proper default fallback.
   *
   * @param key The preferences key to retrieve
   * @return The value associated with the key, or null if not present
   */
  suspend fun <T> getValue(key: Preferences.Key<T>): T? {
    return preferences.data.first()[key]
  }

  companion object {
    const val DEFAULT_SEARCH_SORT = "newest"
  }
}
