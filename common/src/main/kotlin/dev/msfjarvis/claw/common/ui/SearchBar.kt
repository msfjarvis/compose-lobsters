/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
fun SearchBar(
  value: String,
  onValueChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  TextField(
    value = value,
    onValueChange = onValueChange,
    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    textStyle = MaterialTheme.typography.bodyLarge,
    placeholder = { Text(text = "Search") },
    trailingIcon = {
      IconButton(onClick = { onSearch(value) }) {
        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
      }
    },
    keyboardActions = KeyboardActions(onSearch = { onSearch(value) }),
    keyboardOptions =
      KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Search,
      ),
    singleLine = true,
    modifier = modifier.focusable(),
  )
}

@DevicePreviews
@ThemePreviews
@Composable
fun SearchBarPreview() {
  LobstersTheme {
    Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(8.dp)) {
      var value by remember { mutableStateOf("") }
      SearchBar(
        value = value,
        onValueChange = { value = it },
        onSearch = {},
        modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
      )
    }
  }
}
