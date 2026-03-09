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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import io.sentry.Sentry
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
    SectionHeader(title = stringResource(R.string.data_management))
    ListItem(
      headlineContent = { Text(stringResource(R.string.data_transfer)) },
      supportingContent = { Text(stringResource(R.string.import_and_export_your_saved_posts)) },
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
    SectionHeader(title = stringResource(R.string.app_information))
    ListItem(
      headlineContent = { Text(stringResource(R.string.version)) },
      supportingContent = {
        Text(
          stringResource(
            R.string.version_placeholder,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
          )
        )
      },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.Code,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.saved_posts)) },
      supportingContent = { Text(stringResource(R.string.posts_saved_locally, savedPostsCount)) },
      leadingContent = {
        Icon(
          imageVector = Icons.Filled.Bookmarks,
          contentDescription = null,
          modifier = Modifier.height(32.dp),
        )
      },
    )
    ListItem(
      headlineContent = { Text(stringResource(R.string.tag_filtering)) },
      supportingContent = { Text(stringResource(R.string.filter_posts_by_tags)) },
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
    SectionHeader(title = stringResource(R.string.about))
    ListItem(
      headlineContent = { Text(stringResource(R.string.libraries)) },
      supportingContent = {
        Text(stringResource(R.string.view_open_source_libraries_used_in_this_))
      },
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
      headlineContent = { Text(stringResource(R.string.source_code)) },
      supportingContent = { Text(stringResource(R.string.view_the_source_code_on_github)) },
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
        try {
          openInputStream(uri)?.use { stream ->
            val result = importPosts(stream)
            if (result.isSuccess) {
              snackbarHostState.showSnackbarDismissing("Successfully imported posts")
            } else {
              snackbarHostState.showSnackbarDismissing("Failed to import posts")
            }
          } ?: snackbarHostState.showSnackbarDismissing("Unable to open selected file")
        } catch (e: Exception) {
          snackbarHostState.showSnackbarDismissing("Failed to open file: ${e.message}")
        }
      }
    }
  OutlinedButton(
    onClick = {
      importAction.launch(JSON_MIME_TYPE)
      Sentry.metrics().count("import", 1.0)
    },
    shape = MaterialTheme.shapes.extraSmall,
    modifier = modifier,
  ) {
    Text(text = stringResource(R.string.import_action))
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
        try {
          openOutputStream(uri)?.use { stream ->
            exportPostsAsJson(stream)
            snackbarHostState.showSnackbarDismissing("Successfully exported posts")
            Sentry.metrics().count("json_export_success", 1.0)
          }
            ?: run {
              snackbarHostState.showSnackbarDismissing("Unable to open output file")
              Sentry.metrics().count("json_export_failure", 1.0)
            }
        } catch (e: Exception) {
          snackbarHostState.showSnackbarDismissing("Failed to export posts: ${e.message}")
          Sentry.metrics().count("json_export_failure", 1.0)
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
        try {
          openOutputStream(uri)?.use { stream ->
            exportPostsAsHtml(stream)
            snackbarHostState.showSnackbarDismissing("Successfully exported posts")
            Sentry.metrics().count("html_export_success", 1.0)
          }
            ?: run {
              snackbarHostState.showSnackbarDismissing("Unable to open output file")
              Sentry.metrics().count("html_export_failure", 1.0)
            }
        } catch (e: Exception) {
          snackbarHostState.showSnackbarDismissing("Failed to export posts: ${e.message}")
          Sentry.metrics().count("html_export_failure", 1.0)
        }
      }
    }
  var expanded by remember { mutableStateOf(false) }
  OutlinedButton(
    onClick = { expanded = true },
    shape = MaterialTheme.shapes.extraSmall,
    modifier = modifier,
  ) {
    Text(text = stringResource(R.string.export))
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { Text(stringResource(R.string.json)) },
        onClick = {
          expanded = false
          jsonExportAction.launch("claw-export.json")
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Code,
            contentDescription = stringResource(R.string.export_as_json),
          )
        },
      )
      DropdownMenuItem(
        text = { Text(stringResource(R.string.bookmarks)) },
        onClick = {
          expanded = false
          htmlExportAction.launch("claw-export.html")
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Filled.Bookmarks,
            contentDescription = stringResource(R.string.export_as_browser_bookmarks),
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
