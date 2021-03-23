package dev.msfjarvis.lobsters.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClawPreferences @Inject constructor(
  private val dataStore: DataStore<Preferences>,
) {
  private val sortKey = booleanPreferencesKey("post_sorting_order")

  val sortingOrder: Flow<Boolean>
    get() = dataStore.data.map { preferences -> preferences[sortKey] ?: false }

  suspend fun toggleSortingOrder() {
    dataStore.edit { preferences ->
      preferences[sortKey] = (preferences[sortKey] ?: false).not()
    }
  }
}
