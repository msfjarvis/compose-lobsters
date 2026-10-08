/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
@NonRestartableComposable
internal fun HtmlTextPreview() {
  HtmlTextPreviewContent()
}

@Composable
internal fun HtmlTextPreviewContent(dark: Boolean = false) {
  MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
    Surface {
      HtmlText(
        html =
          """
          <h2>Formatting &amp; links</h2>
          <p>Plain <b>bold <strong>black</strong></b> <em>italic</em> <u><s>both</s></u>.
          <big>Big</big> <small>small</small> x<sup>2</sup> H<sub>2</sub>O.
          Unicode: 🙂 日本語 العربية &nbsp; preserved.</p>
          <div>Generic boundary</div><section>without a blank line</section>
          <p>A <a href="https://example.com/long-destination"><code>styled link with wrapping words</code></a><br><br>Repeated breaks remain.</p>
          <pre>  indent
            <i>styled code</i>
          </pre>
          <dl><dt>Term</dt><dd>Description with a leading indentation and a continuation line.</dd></dl>
          """
            .trimIndent(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        linkStyles =
          TextLinkStyles(
            SpanStyle(
              color = MaterialTheme.colorScheme.onSurface,
              background = MaterialTheme.colorScheme.surfaceVariant,
              fontWeight = FontWeight.Bold,
              textDecoration = TextDecoration.Underline,
            )
          ),
        quoteBarColor = MaterialTheme.colorScheme.outlineVariant,
      )
    }
  }
}
