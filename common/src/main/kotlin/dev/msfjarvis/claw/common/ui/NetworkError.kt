/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import io.github.aakira.napier.Napier

@Composable
fun NetworkError(label: String, error: Throwable, modifier: Modifier = Modifier) {
  LaunchedEffect(Unit) { Napier.e(error, "NetworkError") { "Failed to load posts" } }
  var showDialog by remember { mutableStateOf(false) }
  Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    Button(
      onClick = { showDialog = true },
      modifier = Modifier.align(Alignment.CenterHorizontally),
    ) {
      Text(text = "Show error")
    }
  }
  if (showDialog) {
    val clipboard = LocalClipboardManager.current
    AlertDialog(
      onDismissRequest = { showDialog = false },
      confirmButton = {
        Text(
          text = "Copy stacktrace",
          modifier =
            Modifier.clickable {
              clipboard.setText(AnnotatedString(error.stackTraceToString()))
              showDialog = false
            },
        )
      },
      text = { Text(text = "${error.message}", style = MaterialTheme.typography.bodyLarge) },
    )
  }
}

@ThemePreviews
@Composable
internal fun NetworkErrorPreview() {
  LobstersTheme { NetworkError(label = "Failed to load posts", error = Throwable("Preview")) }
}
