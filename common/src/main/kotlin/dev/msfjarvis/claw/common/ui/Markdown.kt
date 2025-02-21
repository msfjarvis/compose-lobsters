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
    <h1>Hello <strong>HTML Converter</strong> for Compose</h1>
    <p>This the first paragraph of the sample app running on <strong>Nothing</strong>!</p>
    <ul>
        <li><strong>Bold</strong></li>
        <li><em>Italic</em></li>
        <li><u>Underline</u></li>
        <li><del>Strikethrough</del></li>
        <li><code>Code</code></li>
        <li><a href="https://www.wikipedia.org/">Hyperlink with custom styling</a></li>
        <li><big>Bigger</big> and <small>smaller</small> text</li>
        <li><sup>Super</sup>text and <sub>sub</sub>text</li>
        <li>A nested ordered list:
            <ol>
                <li>Item 1</li>
                <li>Item 2</li>
            </ol>
        </li>
    </ul>
    <dl>
        <dt>Term</dt>
        <dd>Description.</dd>
    </dl>
    A few HTML entities: &raquo; &copy; &laquo; &check;
    <blockquote>A blockquote, indented relatively to the main text.</blockquote>
    <pre>Preformatted text, preserving
    line breaks...    and spaces.</pre>
    <p>You reached the end of the document.<br />Thank you for reading!</p>
    """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
