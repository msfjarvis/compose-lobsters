/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
fun PasswordField(
  value: String,
  label: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var isPasswordVisible by remember { mutableStateOf(false) }
  TextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label) },
    visualTransformation =
      if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    trailingIcon = {
      Icon(
        painter =
          if (isPasswordVisible) rememberVectorPainter(Icons.Filled.VisibilityOff)
          else rememberVectorPainter(Icons.Filled.Visibility),
        contentDescription = null,
        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
      )
    },
    modifier = modifier,
  )
}

@ThemePreviews
@Composable
private fun PasswordFieldPreview() {
  LobstersTheme {
    var value by remember { mutableStateOf("") }
    PasswordField(
      value = value,
      label = "Password",
      onValueChange = { value = it },
    )
  }
}
