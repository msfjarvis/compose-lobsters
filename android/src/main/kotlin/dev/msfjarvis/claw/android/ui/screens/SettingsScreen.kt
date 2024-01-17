/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.android.ui.datatransfer.SettingsActionItem

@Composable
fun SettingsScreen(
  openLibrariesScreen: () -> Unit,
  openDataTransferScreen: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier.fillMaxSize()) {
    Column {
      SettingsActionItem(
        title = "Data transfer",
        description = "Export and import your saved posts",
        icon = Icons.Filled.ImportExport,
        onClick = openDataTransferScreen,
      )
      SettingsActionItem(
        title = "Libraries",
        icon = Icons.AutoMirrored.Filled.LibraryBooks,
        onClick = openLibrariesScreen,
      )
    }
  }
}
