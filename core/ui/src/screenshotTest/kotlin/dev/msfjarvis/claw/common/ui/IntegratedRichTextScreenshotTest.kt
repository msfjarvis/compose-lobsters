/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme

private val MIXED_HTML = buildString {
  append("""<blockquote><h2>Heading</h2><ol>""")
  repeat(8) { append("<li></li>") }
  append(
    """<li><a href="https://lobste.rs/s/abc123/title">https://lobste.rs/s/abc123/long-link-日本語-🙂-العربية</a></li>
    <li><blockquote><p>十 🙂 العربية</p><p>Second paragraph</p><ul><li>Nested</li></ul></blockquote></li>"""
  )
  repeat(89) { append("<li></li>") }
  append(
    """<li>Hundred</li></ol></blockquote>
    <ul><li><ul><li>List-only parent</li></ul></li></ul><p>Final 日本語 🙂 العربية line.</p>"""
  )
}

@PreviewTest
@Preview(name = "Light 1", widthDp = 240)
@Preview(name = "Dark 1", widthDp = 240, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light 2", widthDp = 240, heightDp = 1800, fontScale = 2f)
@Preview(
  name = "Dark 2",
  widthDp = 240,
  heightDp = 1800,
  fontScale = 2f,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun IntegratedLtr() {
  NestedComment(LayoutDirection.Ltr)
}

@PreviewTest
@Preview(name = "Light 1", widthDp = 240)
@Preview(name = "Dark 1", widthDp = 240, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light 2", widthDp = 240, heightDp = 1800, fontScale = 2f)
@Preview(
  name = "Dark 2",
  widthDp = 240,
  heightDp = 1800,
  fontScale = 2f,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun IntegratedRtl() {
  NestedComment(LayoutDirection.Rtl)
}

// Deterministic depth-three comment shell; real CommentEntry gestures are tested on Android.
@Composable
private fun NestedComment(direction: LayoutDirection) {
  CompositionLocalProvider(LocalLayoutDirection provides direction) {
    LobstersTheme {
      Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(start = 20.dp)) {
          Text(
            "Alice · nested reply",
            Modifier.padding(16.dp),
            style = MaterialTheme.typography.labelLarge,
          )
          ThemedRichText(MIXED_HTML, Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp))
          Text(
            "Reply · Share",
            Modifier.padding(16.dp),
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}
