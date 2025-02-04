/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@Composable
internal fun ThemedRichText(text: String, modifier: Modifier = Modifier) {
  val linkSpanStyle =
    SpanStyle(
      background = MaterialTheme.colorScheme.surfaceVariant,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
      textDecoration = TextDecoration.Underline,
    )
  Text(
    text =
      remember(text) {
        htmlToAnnotatedString(
          html = text,
          compactMode = false,
          style = HtmlStyle(TextLinkStyles(linkSpanStyle)),
        )
      },
    style = MaterialTheme.typography.bodyLarge,
    color = contentColorFor(MaterialTheme.colorScheme.background),
    modifier = modifier,
  )
}

@ThemePreviews
@Composable
internal fun ThemedRichTextPreview() {
  val text =
    """
    <h3 id="heading">Heading</h3>

    <p>This is a paragraph body</p>

    <pre><code>This is a code block</code></pre>

    <p>This is an <code>inline code block</code></p>

    <p><a href="https://github.com/msfjarvis/compose-lobsters">This is a link</a></p>

    <p><img src="https://avatars.githubusercontent.com/u/13348378?v=4" alt="Image"></p>
    """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
