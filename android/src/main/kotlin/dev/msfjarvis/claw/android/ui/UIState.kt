/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import androidx.compose.runtime.Stable
import dev.msfjarvis.claw.core.database.RemoteSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

/**
 * UI state management class that provides access to user preferences and derived state.
 * This class safely handles null values from RemoteSettings by providing default fallback values.
 */
@Stable
@Inject
@SingleIn(AppScope::class)
class UIState(private val remoteSettings: RemoteSettings) {
  /**
   * Flow of the current search sort order. This property safely handles null values from
   * RemoteSettings by providing a default fallback value (DEFAULT_SEARCH_SORT).
   *
   * The flow is already null-safe because RemoteSettings.getSearchSort() returns a Flow<String>
   * with a default value applied using the Elvis operator.
   */
  val searchSort: Flow<String>
    get() = remoteSettings.getSearchSort()
}
