/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.shiori

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.android.viewmodel.ShioriRepository
import dev.msfjarvis.claw.common.ui.PasswordField

@Composable
fun ShioriConfigScreen(
  repository: ShioriRepository,
  modifier: Modifier = Modifier,
) {
  var url by remember { mutableStateOf(repository.getUrl()) }
  var username by remember { mutableStateOf(repository.getUsername()) }
  var password by remember { mutableStateOf(repository.getPassword()) }

  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier.fillMaxSize().padding(all = 16.dp),
  ) {
    TextField(
      value = url,
      label = { Text(text = "Shiori URL") },
      onValueChange = { url = it },
      modifier = Modifier.fillMaxWidth(),
    )
    TextField(
      value = username,
      label = { Text("Username") },
      onValueChange = { username = it },
      modifier = Modifier.fillMaxWidth(),
    )
    PasswordField(
      value = password,
      label = "Password",
      onValueChange = { password = it },
      modifier = Modifier.fillMaxWidth(),
    )
    Button(onClick = { repository.configure(url, username, password) }) { Text(text = "Save") }
  }
}
