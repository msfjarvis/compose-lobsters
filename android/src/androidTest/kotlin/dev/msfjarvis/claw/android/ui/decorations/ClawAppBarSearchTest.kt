/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ClawAppBarSearchTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun detailSceneWithoutVisibleTopLevelShowsBackAffordance() {
    var popped = false
    compose.setContent {
      MaterialTheme {
        ClawAppBar(
          activity = null,
          isTopLevelVisible = false,
          mode = ClawTopBarMode.Browsing,
          navigateTo = {},
          popBackStack = {
            popped = true
            null
          },
          onStartSearch = {},
          onDismissSearch = {},
          onQueryChange = {},
          onSearch = {},
          content = {},
        )
      }
    }

    compose.onNodeWithContentDescription("Go back to previous screen").performClick()
    compose.runOnIdle { assertEquals(true, popped) }
  }

  @Test
  fun restoredQueryIsVisibleAndEditsAndClearReachOwner() {
    var query by mutableStateOf("compose")
    compose.setContent {
      MaterialTheme {
        ClawAppBar(
          activity = null,
          isTopLevelVisible = true,
          mode = ClawTopBarMode.Searching(query, requestFocus = false),
          navigateTo = {},
          popBackStack = { null },
          onStartSearch = {},
          onDismissSearch = {},
          onQueryChange = { query = it },
          onSearch = {},
          content = {},
        )
      }
    }
    compose.onAllNodes(hasSetTextAction())[0].assertTextContains("compose")
    compose.onAllNodes(hasSetTextAction())[0].performTextReplacement("compose test")
    compose.runOnIdle { assertEquals("compose test", query) }
    compose.onAllNodesWithContentDescription("Close search")[0].performClick()
    compose.onAllNodes(hasSetTextAction())[0].assertTextContains("")
    compose.runOnIdle { assertEquals("", query) }
  }
}
