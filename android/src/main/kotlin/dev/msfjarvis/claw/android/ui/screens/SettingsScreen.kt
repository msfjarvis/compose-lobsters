/*
 * Copyright © Harsh Shandilya.
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val JSON_MIME_TYPE = "application/json"
private const val HTML_MIME_TYPE = "application/html"

@Composable
fun SettingsScreen(
  openLibrariesScreen: () -> Unit,
  openRepository: () -> Unit,
  openTagFiltering: () -> Unit,
  snackbarHostState: SnackbarHostState,
  openInputStream: (Uri) -> InputStream?,
  openOutputStream: (Uri) -> OutputStream?,
  importPosts: suspend (InputStream) -> Result<Unit>,
  exportPostsAsJson: suspend (OutputStream) -> Unit,
  exportPostsAsHtml: suspend (OutputStream) -> Unit,
  contentPadding: PaddingValues,
  savedPostsCount: Long,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  Column(modifier.padding(contentPadding)) {
    // Data Management Section
    SectionHeader(title = "Data Management")
    ListItem(
      headlineContent = { Text("Data transfer") },
      supportingContent = { Text("Import and export your saved posts") },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.ImportExport,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
      trailingContent = {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

    Spacer(modifier = Modifier.height(24.dp))

    // App Information Section
    SectionHeader(title = "App Information")
    ListItem(
      headlineContent = { Text("Version") },
      supportingContent = { Text("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.Code,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
    )
    ListItem(
      headlineContent = { Text("Saved posts") },
      supportingContent = { Text("$savedPostsCount posts saved locally") },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.Bookmarks,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
    )
    ListItem(
      headlineContent = { Text("Tag filtering") },
      supportingContent = { Text("Filter posts by tags") },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.FilterList,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
      modifier = Modifier.clickable(onClick = openTagFiltering),
    )

    Spacer(modifier = Modifier.height(24.dp))

    // About Section
    SectionHeader(title = "About")
    ListItem(
      headlineContent = { Text("Libraries") },
      supportingContent = { Text("View open source libraries used in this app") },
      leadingContent = {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
      modifier = Modifier.clickable(onClick = openLibrariesScreen),
    )
    ListItem(
      headlineContent = { Text("Source code") },
      supportingContent = { Text("View the source code on GitHub") },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.Code,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
      modifier = Modifier.clickable(onClick = openRepository),
    )
  }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
    HorizontalDivider(
      modifier = Modifier.padding(horizontal = 16.dp),
      color = MaterialTheme.colorScheme.outlineVariant,
    )
  }
}

@Composable
private fun ImportPosts(
  openInputStream: (Uri) -> InputStream?,
  coroutineScope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  importPosts: suspend (InputStream) -> Result<Unit>,
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
          val result = importPosts(stream)
          if (result.isSuccess) {
            snackbarHostState.showSnackbarDismissing("Successfully imported posts")
          } else {
            snackbarHostState.showSnackbarDismissing("Failed to import posts")
          }
        } ?: snackbarHostState.showSnackbarDismissing("Unable to open selected file")
      }
    }
  OutlinedButton(
    onClick = { importAction.launch(JSON_MIME_TYPE) },
    shape = MaterialTheme.shapes.extraSmall,
    modifier = modifier,
  ) {
    Text(text = "Import")
  }
}

@Composable
private inline fun ExportPosts(
  coroutineScope: CoroutineScope,
  snackbarHostState: SnackbarHostState,
  crossinline openOutputStream: (Uri) -> OutputStream?,
  crossinline exportPostsAsJson: suspend (OutputStream) -> Unit,
  crossinline exportPostsAsHtml: suspend (OutputStream) -> Unit,
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
    modifier = modifier,
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
private suspend inline fun SnackbarHostState.showSnackbarDismissing(text: String) {
  currentSnackbarData?.dismiss()
  showSnackbar(text)
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview() {
  LobstersTheme {
    SettingsScreen(
      openLibrariesScreen = {},
      openRepository = {},
      openTagFiltering = {},
      snackbarHostState = SnackbarHostState(),
      openInputStream = { null },
      openOutputStream = { null },
      importPosts = { Result.success(Unit) },
      exportPostsAsJson = {},
      exportPostsAsHtml = {},
      contentPadding = PaddingValues(),
      savedPostsCount = 42,
    )
  }
}
