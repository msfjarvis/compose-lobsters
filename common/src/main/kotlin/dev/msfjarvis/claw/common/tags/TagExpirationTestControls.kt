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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.R

@Composable
fun TagExpirationTestControls(onTriggerCleanup: () -> Unit, modifier: Modifier = Modifier) {
  Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
    Text(
      text = stringResource(R.string.debug_controls),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.error,
    )
    Button(onClick = onTriggerCleanup, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
      Text(stringResource(R.string.force_cleanup_expired_tags_now))
    }
    Text(
      text = stringResource(R.string.this_will_immediately_run_the_cleanup_wo),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(top = 4.dp),
    )
  }
}
