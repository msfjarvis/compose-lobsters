/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme

// These shells retain each caller's surrounding spacing without network, time, or image loading.
@PreviewTest
@Preview(name = "Light", widthDp = 320)
@Preview(name = "Dark", widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CommentMigration() {
  LobstersTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      Column {
        Text(
          "Alice · 42 · 1h",
          Modifier.padding(16.dp),
          style = MaterialTheme.typography.labelLarge,
        )
        ThemedRichText(
          """<p>A comment with <a href="https://lobste.rs/s/abc123/story">a related story</a>.</p>
          <ul><li><p>First recommendation</p></li><li><p>Second recommendation</p></li>
          <li><ul><li>Nested detail</li></ul></li></ul>
          <blockquote><p>Quoted context</p><p>Another quoted paragraph</p></blockquote>
          <p>Final comment line.</p>""",
          Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        )
        Text(
          "Reply · Share",
          Modifier.padding(horizontal = 16.dp),
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@PreviewTest
@Preview(name = "Light", widthDp = 320)
@Preview(name = "Dark", widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DescriptionMigration() {
  LobstersTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("A post about HTML", style = MaterialTheme.typography.titleLarge)
        Text("programming · web", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(4.dp))
        ThemedRichText(
          """<p>A description with <strong>important details</strong>.</p>
          <ol><li>Read <a href="https://example.com/guide">the guide</a></li><li>Try it locally</li></ol>
          <p>Final description line.</p>"""
        )
        Spacer(Modifier.height(4.dp))
        Text("Submitted by Alice", style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}

@PreviewTest
@Preview(name = "Light", widthDp = 320)
@Preview(name = "Dark", widthDp = 320, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BioMigration() {
  LobstersTheme {
    Surface {
      Column(
        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text("Alice", style = MaterialTheme.typography.displaySmall)
        ThemedRichText(
          """<p>I build <code>small tools</code> and write about them.</p>
          <blockquote>Keep things <em>simple</em>.</blockquote>
          <p>Find me on <a href="https://example.com/">my website</a>.</p>
          <p>Final bio line.</p>"""
        )
        Text("Invited by Bob", style = MaterialTheme.typography.bodyLarge)
      }
    }
  }
}
