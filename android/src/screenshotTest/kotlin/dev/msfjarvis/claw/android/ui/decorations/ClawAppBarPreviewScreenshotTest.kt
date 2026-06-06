/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.decorations

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme

@PreviewTest
@Preview(name = "Browsing")
@Composable
private fun ClawAppBarBrowsingPreviewScreenshot() {
  LobstersTheme {
    ClawAppBar(
      activity = null,
      isTopLevel = true,
      mode = ClawTopBarMode.Browsing,
      navigateTo = {},
      popBackStack = { null },
      onStartSearch = {},
      onDismissSearch = {},
      onExpandedChange = {},
      onQueryChange = {},
      onSearch = {},
      content = { Text("Fake result content") },
    )
  }
}

@PreviewTest
@Preview(name = "Searching")
@Composable
private fun ClawAppBarSearchingPreviewScreenshot() {
  LobstersTheme {
    ClawAppBar(
      activity = null,
      isTopLevel = true,
      mode = ClawTopBarMode.Searching(query = "compose", expanded = true, requestFocus = false),
      navigateTo = {},
      popBackStack = { null },
      onStartSearch = {},
      onDismissSearch = {},
      onExpandedChange = {},
      onQueryChange = {},
      onSearch = {},
      content = { Text("Fake result content") },
    )
  }
}

@PreviewTest
@Preview(name = "SearchingWithResults")
@Composable
private fun ClawAppBarSearchingWithResultsPreviewScreenshot() {
  LobstersTheme {
    ClawAppBar(
      activity = null,
      isTopLevel = true,
      mode = ClawTopBarMode.Searching(query = "compose", expanded = true, requestFocus = false),
      navigateTo = {},
      popBackStack = { null },
      onStartSearch = {},
      onDismissSearch = {},
      onExpandedChange = {},
      onQueryChange = {},
      onSearch = {},
      content = {
        Column {
          Text("Result 1")
          Text("Result 2")
        }
      },
    )
  }
}
