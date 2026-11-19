/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
internal fun RestoreSearchResultsScroll(
  listState: LazyListState,
  requestGeneration: Long,
  hasRestoredSearchState: Boolean,
) {
  var lastScrolledGeneration by rememberSaveable { mutableLongStateOf(0L) }
  LaunchedEffect(hasRestoredSearchState, requestGeneration) {
    if (hasRestoredSearchState) {
      if (requestGeneration > 0 && requestGeneration != lastScrolledGeneration) {
        listState.scrollToItem(0)
      }
      lastScrolledGeneration = requestGeneration
    }
  }
}
