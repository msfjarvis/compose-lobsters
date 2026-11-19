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
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ClawAppBarSearchTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun restoredQueryIsVisibleAndEditsAndClearReachOwner() {
    var query by mutableStateOf("compose")
    compose.setContent {
      MaterialTheme {
        ClawAppBar(
          activity = null,
          isTopLevel = true,
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
    compose.onNode(hasSetTextAction()).assertTextContains("compose")
    compose.onNode(hasSetTextAction()).performTextReplacement("compose test")
    compose.runOnIdle { assertEquals("compose test", query) }
    compose.onNodeWithContentDescription("Close search").performClick()
    compose.onNode(hasSetTextAction()).assertTextContains("")
    compose.runOnIdle { assertEquals("", query) }
  }
}
