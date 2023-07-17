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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val MIME_TYPE = "application/json"

@Composable
fun DataTransferScreen(
  context: Context,
  importPosts: suspend (InputStream) -> Unit,
  exportPosts: suspend (OutputStream) -> Unit,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  Column(modifier = modifier) {
    ImportOption(context, coroutineScope, importPosts, snackbarHostState)
    ExportOption(context, coroutineScope, exportPosts, snackbarHostState)
  }
}

@Composable
private fun ImportOption(
  context: Context,
  coroutineScope: CoroutineScope,
  importPosts: suspend (InputStream) -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  val importAction =
    rememberLauncherForActivityResult(GetContent()) { uri ->
      if (uri == null) {
        coroutineScope.launch { snackbarHostState.showSnackbarDismissing("No file selected") }
        return@rememberLauncherForActivityResult
      }
      coroutineScope.launch {
        context.contentResolver.openInputStream(uri)?.use { stream ->
          importPosts(stream)
          snackbarHostState.showSnackbarDismissing("Successfully imported posts")
        }
      }
    }
  SettingsActionItem(
    title = "Import saved posts",
    description = "Import saved posts from a previously generated export",
    icon = Icons.Filled.Download,
  ) {
    importAction.launch(MIME_TYPE)
  }
}

@Composable
private fun ExportOption(
  context: Context,
  coroutineScope: CoroutineScope,
  exportPosts: suspend (OutputStream) -> Unit,
  snackbarHostState: SnackbarHostState,
) {
  val exportAction =
    rememberLauncherForActivityResult(CreateDocument(MIME_TYPE)) { uri ->
      if (uri == null) {
        coroutineScope.launch { snackbarHostState.showSnackbarDismissing("No file selected") }
        return@rememberLauncherForActivityResult
      }
      coroutineScope.launch {
        context.contentResolver.openOutputStream(uri)?.use { stream ->
          exportPosts(stream)
          snackbarHostState.showSnackbarDismissing("Successfully exported posts")
        }
      }
    }
  SettingsActionItem(
    title = "Export posts to file",
    description = "Write all saved posts into a JSON file that can be imported at a later date",
    icon = Icons.Filled.Upload,
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

/** Shows a Snackbar but dismisses any existing ones first. */
private suspend fun SnackbarHostState.showSnackbarDismissing(
  text: String,
) {
  currentSnackbarData?.dismiss()
  showSnackbar(text)
}
