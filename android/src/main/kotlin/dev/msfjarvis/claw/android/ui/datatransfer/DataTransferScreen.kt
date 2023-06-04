/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.datatransfer

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.android.viewmodel.DataTransferRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val MIME_TYPE = "application/json"

@Composable
fun DataTransferScreen(
  context: Context,
  dataTransferRepository: DataTransferRepository,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  Column(modifier = modifier) {
    ImportOption(context, coroutineScope, dataTransferRepository)
    ExportOption(context, coroutineScope, dataTransferRepository)
  }
}

@Composable
private fun ImportOption(
  context: Context,
  coroutineScope: CoroutineScope,
  dataTransferRepository: DataTransferRepository,
) {
  val importAction =
    rememberLauncherForActivityResult(GetContent()) { uri ->
      if (uri == null) return@rememberLauncherForActivityResult
      coroutineScope.launch {
        context.contentResolver.openInputStream(uri)?.use { stream ->
          dataTransferRepository.importPosts(stream)
        }
      }
    }
  SettingsActionItem(
    title = "Import saved posts",
    description = "Import saved posts from a previously generated export",
    icon = Icons.Outlined.SouthWest,
  ) {
    importAction.launch(MIME_TYPE)
  }
}

@Composable
private fun ExportOption(
  context: Context,
  coroutineScope: CoroutineScope,
  dataTransferRepository: DataTransferRepository,
) {
  val exportAction =
    rememberLauncherForActivityResult(CreateDocument(MIME_TYPE)) { uri ->
      if (uri == null) return@rememberLauncherForActivityResult
      coroutineScope.launch {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
          dataTransferRepository.exportPosts(stream)
        }
      }
    }
  SettingsActionItem(
    title = "Export posts to file",
    description = "Write all saved posts into a JSON file that can be imported at a later date",
    icon = Icons.Outlined.NorthEast,
  ) {
    exportAction.launch("claw-export.json")
  }
}

@Composable
private fun SettingsActionItem(
  title: String,
  modifier: Modifier = Modifier,
  description: String? = null,
  icon: ImageVector? = null,
  onClick: (() -> Unit)? = null,
) {
  ListItem(
    headlineContent = { Text(title) },
    supportingContent = { description?.let { Text(it) } },
    leadingContent = {
      icon?.let {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      }
    },
    modifier = modifier.clickable { onClick?.invoke() },
  )
}
