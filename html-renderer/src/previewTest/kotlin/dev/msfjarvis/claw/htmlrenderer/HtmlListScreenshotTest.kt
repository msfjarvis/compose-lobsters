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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(name = "Light LTR", widthDp = 320)
@Preview(name = "Narrow font 2", widthDp = 160, heightDp = 3200, fontScale = 2f)
@Composable
private fun HtmlListsLight() {
  ListPreviewContent(dark = false)
}

@PreviewTest
@Preview(name = "Dark RTL", widthDp = 320)
@Preview(name = "RTL font 2", widthDp = 200, heightDp = 2600, fontScale = 2f)
@Composable
private fun HtmlListsDarkRtl() {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    ListPreviewContent(dark = true)
  }
}

@Composable
private fun ListPreviewContent(dark: Boolean) {
  val numbered =
    (1..100).joinToString("") {
      when (it) {
        9 -> "<li>Nine wraps to the shared content edge</li>"
        10 -> "<li>Ten</li>"
        100 -> "<li>Hundred</li>"
        else -> "<li></li>"
      }
    }
  MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
    Surface {
      HtmlText(
        """
        <p>Before the list</p>
        <ul>
          <li>Our client is facing insurmountable financial difficulties</li>
          <li><ul><li>including job loss and unexpected medical expenses</li></ul></li>
        </ul>
        <p>After the list</p>
        <ul>
          <li><p>Tight A</p></li><li><p>Tight B</p></li>
          <li><p>Distinct paragraph A</p><p>Distinct paragraph B</p></li>
          <li>Prose before<ul><li>Nested<ul><li>Third level</li></ul></li></ul>Prose after</li>
          <li><ul><li>First descendant baseline</li></ul>Following prose</li>
          <li>日本語 العربية <a href="https://example.com/long-link">a long linked continuation</a></li>
        </ul>
        <ol>$numbered</ol>
        <p>Final line remains visible</p>
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
