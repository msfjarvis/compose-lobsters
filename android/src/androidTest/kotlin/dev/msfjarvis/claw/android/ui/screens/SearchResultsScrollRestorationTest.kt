/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchResultsScrollRestorationTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun restorationKeepsPositionAndNewRequestsResetIt() {
    var generation by mutableLongStateOf(1L)
    var searchRestored by mutableStateOf(true)
    lateinit var listState: LazyListState
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      listState = rememberLazyListState()
      RestoreSearchResultsScroll(listState, generation, searchRestored)
      LazyColumn(state = listState) { items(100) { Text("Result $it") } }
    }
    compose.runOnIdle { runBlocking { listState.scrollToItem(30) } }
    compose.runOnIdle { searchRestored = false }
    restoration.emulateSavedInstanceStateRestore()
    compose.runOnIdle { searchRestored = true }
    compose.runOnIdle { assertEquals(30, listState.firstVisibleItemIndex) }
    compose.runOnIdle { generation = 2L }
    compose.runOnIdle { assertEquals(0, listState.firstVisibleItemIndex) }
    compose.runOnIdle { runBlocking { listState.scrollToItem(15) } }
    compose.runOnIdle { generation = 0L }
    compose.runOnIdle { generation = 1L }
    compose.runOnIdle { assertEquals(0, listState.firstVisibleItemIndex) }
  }
}
