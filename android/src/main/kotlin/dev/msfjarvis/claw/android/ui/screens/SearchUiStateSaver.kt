/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import dev.msfjarvis.claw.feature.search.api.SearchUiState

internal val SearchUiStateSaver: Saver<SearchUiState, Any> =
  mapSaver(
    save = { state ->
      mapOf(
        "isActive" to state.isActive,
        "query" to state.query,
        "lastExecutedQuery" to state.lastExecutedQuery,
        "requestGeneration" to state.requestGeneration,
      )
    },
    restore = { saved ->
      SearchUiState(
        isActive = saved.getValue("isActive") as Boolean,
        query = saved.getValue("query") as String,
        lastExecutedQuery = saved["lastExecutedQuery"] as String?,
        requestGeneration = saved.getValue("requestGeneration") as Long,
      )
    },
  )

internal fun shouldDismissSearchOnBack(
  isSearchActive: Boolean,
  isCurrentDestinationTopLevel: Boolean,
): Boolean = isSearchActive && isCurrentDestinationTopLevel
