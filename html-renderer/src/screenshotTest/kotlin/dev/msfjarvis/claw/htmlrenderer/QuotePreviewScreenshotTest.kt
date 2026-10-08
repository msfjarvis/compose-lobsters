/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.android.tools.screenshot.PreviewTest

private const val QUOTE_HTML =
  """
<div>Before</div>
<blockquote><blockquote><p> </p></blockquote></blockquote>
<blockquote>
  <p>Ordinary quote, <b>bold</b>, <i>italic</i>, and <a href="https://example.com">link</a>.</p>
  <p>Second paragraph — 日本語 🙂 العربية.</p>
  <blockquote><h2>Nested heading</h2><pre>  code
    indented</pre><p>Nested end.</p></blockquote>
  <p>Outer end.</p>
</blockquote>
<p>After: no bar here.</p>
"""

@PreviewTest
@Preview(name = "Light supplied red", widthDp = 320)
@Preview(name = "Narrow large font", widthDp = 160, heightDp = 1800, fontScale = 2f)
@Composable
private fun QuotesLight() {
  Box(Modifier.background(Color.White)) {
    HtmlText(
      QUOTE_HTML,
      TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
      Color.Black,
      TextLinkStyles(SpanStyle(color = Color.Blue)),
      Color.Red,
    )
  }
}

@PreviewTest
@Preview(name = "Dark RTL supplied yellow", widthDp = 320, heightDp = 1200, fontScale = 2f)
@Composable
private fun QuotesDarkRtl() {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(Modifier.background(Color.Black)) {
      HtmlText(
        QUOTE_HTML,
        TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        Color.White,
        TextLinkStyles(SpanStyle(color = Color.Cyan)),
        Color.Yellow,
      )
    }
  }
}

@PreviewTest
@Preview(name = "Clamped 10 dp", widthDp = 10, heightDp = 180)
@Composable
private fun QuotesClamped() {
  Box(Modifier.background(Color.White)) {
    HtmlText(
      "<blockquote><blockquote>x</blockquote></blockquote>",
      TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
      Color.Black,
      TextLinkStyles(),
      Color.Red,
    )
  }
}

@PreviewTest
@Preview(name = "Empty quotes", widthDp = 160)
@Composable
private fun QuotesEmpty() {
  Box(Modifier.background(Color.White)) {
    HtmlText(
      "<div>A<blockquote><p> </p><blockquote> </blockquote></blockquote>B</div>",
      TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
      Color.Black,
      TextLinkStyles(),
      Color.Red,
    )
  }
}
