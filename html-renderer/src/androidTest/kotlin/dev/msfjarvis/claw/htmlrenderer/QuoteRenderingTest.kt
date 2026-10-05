/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class QuoteRenderingTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun nestedBarsStayVisibleOnLogicalLeadingEdgeAfterDirectionAndColorChanges() {
    val direction = mutableStateOf(LayoutDirection.Ltr)
    val color = mutableStateOf(Color.Red)
    compose.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides direction.value) {
        Subject(
          "<blockquote><p>Outer</p><blockquote><p>Inner</p><p>End</p></blockquote></blockquote><p>After</p>",
          quoteColor = color.value,
        )
      }
    }
    for (rtl in listOf(false, true)) {
      compose.runOnIdle {
        direction.value = if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        color.value = if (rtl) Color.Blue else Color.Red
      }
      val outer = compose.onNodeWithText("Outer").fetchSemanticsNode().boundsInRoot
      val inner = compose.onNodeWithText("Inner").fetchSemanticsNode().boundsInRoot
      val end = compose.onNodeWithText("End").fetchSemanticsNode().boundsInRoot
      val after = compose.onNodeWithText("After").fetchSemanticsNode().boundsInRoot
      val dp = compose.density.density
      val inset = if (rtl) outer.right - inner.right else inner.left - outer.left
      assertThat(inset).isWithin(.5f).of(15 * dp)
      val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
      val leading = if (rtl) pixels.width - 1 else 0
      val nested = if (rtl) pixels.width - 1 - (15 * dp).toInt() else (15 * dp).toInt()
      for (y in 0 until end.bottom.toInt()) assertThat(pixels[leading, y]).isEqualTo(color.value)
      for (y in inner.top.toInt() until end.bottom.toInt()) assertThat(pixels[nested, y])
        .isEqualTo(color.value)
      for (y in end.bottom.toInt() until after.top.toInt()) assertThat(pixels[leading, y])
        .isNotEqualTo(color.value)
      assertThat(pixels[if (rtl) leading - (4 * dp).toInt() else (4 * dp).toInt(), 0])
        .isNotEqualTo(color.value)
    }
  }

  @Test
  fun emptyQuotesHaveNoSpaceDecorationOrSemantics() {
    compose.setContent {
      Subject("<div>A<blockquote><blockquote><p> </p></blockquote></blockquote>B</div>")
    }
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(a.bottom).isEqualTo(b.top)
    assertThat(a.left).isEqualTo(b.left)
    val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
    for (y in 0 until pixels.height) for (x in 0 until pixels.width) assertThat(pixels[x, y])
      .isNotEqualTo(Color.Red)
    assertThat(
        compose
          .onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Text),
            useUnmergedTree = true,
          )
          .fetchSemanticsNodes()
      )
      .hasSize(2)
  }

  @Test
  fun explicitHardBreakQuoteStillOccupiesARealTextLineWithABar() {
    compose.setContent { Subject("<blockquote><br></blockquote><p>After</p>") }
    val line = compose.onNodeWithText("\n").fetchSemanticsNode()
    assertThat(layoutResult("\n").size.height).isGreaterThan(0)
    val after = compose.onNodeWithText("After").fetchSemanticsNode().boundsInRoot
    assertThat(after.top).isGreaterThan(line.boundsInRoot.bottom)
    val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
    assertThat(pixels[0, 0]).isEqualTo(Color.Red)
  }

  @Test
  fun subInsetWidthsClampBarAndContentEvenWithNestedQuotes() {
    val width = mutableStateOf(1.dp)
    compose.setContent {
      Subject("<blockquote><blockquote>x</blockquote></blockquote>", Modifier.width(width.value))
    }
    for (w in listOf(0, 1, 2, 3, 10, 14, 15)) {
      compose.runOnIdle { width.value = w.dp }
      val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
      val text = compose.onNodeWithText("x").fetchSemanticsNode()
      assertThat(root.width).isWithin(.5f).of(with(compose.density) { w.dp.toPx() })
      assertThat(layoutResult("x").size.width).isEqualTo(0)
      // Clipped semantics bounds collapse to Rect.Zero for zero-width text.
      assertThat(text.positionInRoot.x).isWithin(.5f).of(root.right)
      if (w > 0) {
        val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
        val bar = with(compose.density) { minOf(w, 3).dp.roundToPx() }
        for (x in 0 until pixels.width) {
          if (x < bar) assertThat(pixels[x, 0]).isEqualTo(Color.Red)
          else assertThat(pixels[x, 0]).isNotEqualTo(Color.Red)
        }
      }
    }
  }

  @Test
  fun ordinaryTextHeadingCodeAndLinksKeepNativeSemanticsAndBarsDontInterceptInput() {
    var parentClicks = 0
    val opened = mutableListOf<String>()
    compose.setContent {
      CompositionLocalProvider(
        LocalUriHandler provides
          object : UriHandler {
            override fun openUri(uri: String) {
              opened += uri
            }
          }
      ) {
        Subject(
          "<blockquote><p>Plain</p><h2>Heading</h2><pre>  code\n</pre><p><a href='target'>Link</a></p></blockquote>",
          Modifier.clickable { parentClicks++ },
        )
      }
    }
    compose
      .onNodeWithText("Heading", useUnmergedTree = true)
      .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    val plain = layoutResult("Plain")
    assertThat(plain.layoutInput.style.fontStyle).isNotEqualTo(FontStyle.Italic)
    assertThat(plain.layoutInput.text.spanStyles.any { it.item.fontStyle == FontStyle.Italic })
      .isFalse()
    val code =
      compose
        .onNodeWithText("  code\n", useUnmergedTree = true)
        .fetchSemanticsNode()
        .config[SemanticsProperties.Text]
        .single()
    assertThat(code.spanStyles.any { it.item.fontFamily == FontFamily.Monospace }).isTrue()
    assertThat(
        compose
          .onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Text),
            useUnmergedTree = true,
          )
          .fetchSemanticsNodes()
          .map { it.config[SemanticsProperties.Text].single().text }
      )
      .containsExactly("Plain", "Heading", "  code\n", "Link")
      .inOrder()
    assertThat(
        compose
          .onAllNodes(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription),
            useUnmergedTree = true,
          )
          .fetchSemanticsNodes()
      )
      .isEmpty()
    val link = compose.onAllNodes(hasClickAction(), useUnmergedTree = true)
    assertThat(link.fetchSemanticsNodes()).hasSize(2)
    link[1].performClick()
    assertThat(opened).containsExactly("target")
    compose.onNodeWithTag("document").performTouchInput { click(Offset(1f, 1f)) }
    assertThat(parentClicks).isEqualTo(1)
  }

  @Test
  fun quoteIntrinsicsIncludeOneInsetAndInternalGapAndMatchMeasuredHeight() {
    var verified = false
    compose.setContent {
      val inset = with(LocalDensity.current) { 15.dp.roundToPx() }
      Layout(
        content = {
          Subject("<blockquote><p>long words wrap here</p><p>end</p></blockquote>")
          Subject("<p>long words wrap here</p><p>end</p>")
        }
      ) { measurables, _ ->
        val quote = measurables[0]
        val plain = measurables[1]
        assertThat(quote.minIntrinsicWidth(Constraints.Infinity))
          .isEqualTo(plain.minIntrinsicWidth(Constraints.Infinity) + inset)
        assertThat(quote.maxIntrinsicWidth(Constraints.Infinity))
          .isEqualTo(plain.maxIntrinsicWidth(Constraints.Infinity) + inset)
        val width = 100.dp.roundToPx()
        val minHeight = quote.minIntrinsicHeight(width)
        val maxHeight = quote.maxIntrinsicHeight(width)
        assertThat(minHeight).isEqualTo(plain.minIntrinsicHeight(width - inset))
        assertThat(maxHeight).isEqualTo(plain.maxIntrinsicHeight(width - inset))
        val placed = quote.measure(Constraints(maxWidth = width))
        assertThat(placed.height).isEqualTo(minHeight)
        assertThat(placed.height).isEqualTo(maxHeight)
        verified = true
        layout(placed.width, placed.height) { placed.place(0, 0) }
      }
    }
    compose.waitForIdle()
    assertThat(verified).isTrue()
  }

  @Test
  fun nestedQuoteForwardsBothBaselinesAndIntrinsicHeightAtLargeFontScale() {
    var first = 0
    var last = 0
    compose.setContent {
      CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, 2f)) {
        Row(Modifier.width(IntrinsicSize.Max)) {
          Subject(
            "<blockquote><h2>Heading</h2><blockquote><p>Last</p></blockquote></blockquote>",
            Modifier.height(IntrinsicSize.Max).alignByBaseline().layout { measurable, constraints ->
              val placed = measurable.measure(constraints)
              first = placed[FirstBaseline]
              last = placed[LastBaseline]
              layout(placed.width, placed.height) { placed.place(0, 0) }
            },
          )
          Text(
            "Ref",
            Modifier.alignByBaseline(),
            style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
          )
        }
      }
    }
    val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
    val heading = compose.onNodeWithText("Heading").fetchSemanticsNode().boundsInRoot
    val end = compose.onNodeWithText("Last").fetchSemanticsNode().boundsInRoot
    val ref = compose.onNodeWithText("Ref").fetchSemanticsNode().boundsInRoot
    assertThat(root.top + first)
      .isWithin(.5f)
      .of(heading.top + layoutResult("Heading").firstBaseline)
    assertThat(root.top + last).isWithin(.5f).of(end.top + layoutResult("Last").lastBaseline)
    assertThat(root.top + first).isWithin(.5f).of(ref.top + layoutResult("Ref").firstBaseline)
    assertThat(root.bottom).isEqualTo(end.bottom)
  }

  private fun layoutResult(text: String): TextLayoutResult {
    val results = mutableListOf<TextLayoutResult>()
    compose.onNodeWithText(text, useUnmergedTree = true).performSemanticsAction(
      SemanticsActions.GetTextLayoutResult
    ) {
      it(results)
    }
    return results.single()
  }

  @Test
  fun barWrapsMultipleParagraphsIncludingInternalGapButExcludesSiblingGap() {
    compose.setContent {
      Subject("<blockquote><p>First</p><p>Second</p></blockquote><p>Outside</p>")
    }
    val first = compose.onNodeWithText("First").fetchSemanticsNode().boundsInRoot
    val second = compose.onNodeWithText("Second").fetchSemanticsNode().boundsInRoot
    val outside = compose.onNodeWithText("Outside").fetchSemanticsNode().boundsInRoot
    val dp = compose.density.density
    assertThat(first.left - outside.left).isWithin(.5f).of(15 * dp)
    assertThat(second.left).isEqualTo(first.left)
    assertThat(second.top - first.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
    assertThat(outside.top - second.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
    val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
    for (y in 0 until second.bottom.toInt()) {
      assertThat(pixels[0, y]).isEqualTo(Color.Red)
      assertThat(pixels[(3 * dp).toInt() - 1, y]).isEqualTo(Color.Red)
      assertThat(pixels[(4 * dp).toInt(), y]).isNotEqualTo(Color.Red)
    }
    for (y in second.bottom.toInt() until pixels.height) {
      assertThat(pixels[0, y]).isNotEqualTo(Color.Red)
    }
  }
}

@Composable
private fun Subject(html: String, modifier: Modifier = Modifier, quoteColor: Color = Color.Red) {
  HtmlText(
    html,
    style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    color = Color.Black,
    linkStyles = TextLinkStyles(),
    quoteBarColor = quoteColor,
    modifier = modifier.testTag("document"),
  )
}
