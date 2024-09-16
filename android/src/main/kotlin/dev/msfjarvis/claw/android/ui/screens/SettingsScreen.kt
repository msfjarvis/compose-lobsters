/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val JSON_MIME_TYPE = "application/json"
private const val HTML_MIME_TYPE = "application/html"

@Composable
fun SettingsScreen(
  openLibrariesScreen: () -> Unit,
  snackbarHostState: SnackbarHostState,
  openInputStream: (Uri) -> InputStream?,
  openOutputStream: (Uri) -> OutputStream?,
  importPosts: suspend (InputStream) -> Unit,
  exportPostsAsJson: suspend (OutputStream) -> Unit,
  exportPostsAsHtml: suspend (OutputStream) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  Box(modifier = modifier.padding(contentPadding)) {
    Column {
      ListItem(
        headlineContent = { Text("Data transfer") },
        leadingContent = {
          Icon(
            imageVector = Icons.Filled.ImportExport,
            contentDescription = null,
            modifier = Modifier.height(32.dp),
          )
        },
        supportingContent = {
          Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            ImportPosts(openInputStream, coroutineScope, snackbarHostState, importPosts)
            ExportPosts(
              coroutineScope,
              snackbarHostState,
              openOutputStream,
              exportPostsAsJson,
              exportPostsAsHtml,
            )
          }
        },
      )
      ListItem(
        headlineContent = { Text("Libraries") },
        leadingContent = {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
            contentDescription = null,
            modifier = Modifier.height(32.dp),
          )
        },
        modifier = Modifier.clickable(onClick = openLibrariesScreen),
      )
    }
  }
}

@Composable
private fun RowScope.ImportPosts(
  openInputStream: (Uri) -> InputStream?,
  coroutineScope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  importPosts: suspend (InputStream) -> Unit,
  modifier: Modifier = Modifier,
) {
  val importAction =
    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
      if (uri == null) {
        coroutineScope.launch { snackbarHostState.showSnackbarDismissing("No file selected") }
        return@rememberLauncherForActivityResult
      }
      coroutineScope.launch {
        openInputStream(uri)?.use { stream ->
          importPosts(stream)
          snackbarHostState.showSnackbarDismissing("Successfully imported posts")
        }
      }
    }
  OutlinedButton(
    onClick = { importAction.launch(JSON_MIME_TYPE) },
    shape = MaterialTheme.shapes.extraSmall,
    modifier = modifier.fillMaxWidth(0.30f).weight(0.5f),
  ) {
    Text(text = "Import")
  }
}

@Composable
private fun RowScope.ExportPosts(
  coroutineScope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  openOutputStream: (Uri) -> OutputStream?,
  exportPostsAsJson: suspend (OutputStream) -> Unit,
  exportPostsAsHtml: suspend (OutputStream) -> Unit,
  modifier: Modifier = Modifier,
) {
  val jsonExportAction =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(JSON_MIME_TYPE)) { uri
      ->
      if (uri == null) {
        coroutineScope.launch { snackbarHostState.showSnackbarDismissing("No file selected") }
        return@rememberLauncherForActivityResult
      }
      coroutineScope.launch {
        openOutputStream(uri)?.use { stream ->
          exportPostsAsJson(stream)
          snackbarHostState.showSnackbarDismissing("Successfully exported posts")
        }
      }
    }
  val htmlExportAction =
    rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(HTML_MIME_TYPE)) { uri
      ->
      if (uri == null) {
        coroutineScope.launch { snackbarHostState.showSnackbarDismissing("No file selected") }
        return@rememberLauncherForActivityResult
      }
      coroutineScope.launch {
        openOutputStream(uri)?.use { stream ->
          exportPostsAsHtml(stream)
          snackbarHostState.showSnackbarDismissing("Successfully exported posts")
        }
      }
    }
  var expanded by remember { mutableStateOf(false) }
  OutlinedButton(
    onClick = { expanded = true },
    shape = MaterialTheme.shapes.extraSmall,
    modifier = modifier.fillMaxWidth(0.30f).weight(0.5f),
  ) {
    Text(text = "Export")
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { Text("JSON") },
        onClick = {
          expanded = false
          jsonExportAction.launch("claw-export.json")
        },
        leadingIcon = {
          Icon(imageVector = Icons.Filled.Code, contentDescription = "Export as JSON")
        },
      )
      DropdownMenuItem(
        text = { Text("Bookmarks") },
        onClick = {
          expanded = false
          htmlExportAction.launch("claw-export.html")
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Bookmarks,
            contentDescription = "Export as browser bookmarks",
          )
        },
      )
    }
  }
}

/** Shows a Snackbar but dismisses any existing ones first. */
private suspend fun SnackbarHostState.showSnackbarDismissing(text: String) {
  currentSnackbarData?.dismiss()
  showSnackbar(text)
}
