/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TagExpirationTestControls(onTriggerCleanup: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(
      text = "Debug Controls",
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.error,
    )
    Button(onClick = onTriggerCleanup, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
      Text("Force Cleanup Expired Tags Now")
    }
    Text(
      text = "This will immediately run the cleanup worker to remove expired tag blocks",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = 4.dp),
    )
  }
}
