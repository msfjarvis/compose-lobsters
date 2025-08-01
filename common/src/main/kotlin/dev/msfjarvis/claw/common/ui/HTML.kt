/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import io.github.malikshairali.nativehtml.parser.HTMLParser

@Composable
internal fun ThemedRichText(text: String, modifier: Modifier = Modifier) {
  val elements = remember { HTMLParser().parse(text.replace("&lt;", "<").replace("&gt;", ">")) }
  Column(modifier.fillMaxSize()) { elements.forEach { it.render() } }
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
    <pre><code class="language-toml">Preformatted text, preserving
    line breaks...    and spaces.</code></pre>
    <p><code>Inline code example.</code> You reached the end of the document.<br />Thank you for reading!</p>
    """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
