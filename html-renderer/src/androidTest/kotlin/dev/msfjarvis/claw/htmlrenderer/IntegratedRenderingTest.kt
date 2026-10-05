/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class IntegratedRenderingTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun mixedNestingPreservesGapsBaselinesIndentationSilentBarsAndSourceOrder() {
    compose.setContent {
      HtmlText(
        """<blockquote><ul><li><blockquote><h2>First</h2><p>Second</p>
        <ul><li>Deep</li></ul></blockquote></li><li><p>Tight</p></li>
        <li><ul><li>Suppressed</li></ul></li></ul></blockquote><p>Outside</p>""",
        TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        Color.Black,
        TextLinkStyles(),
        Color.Red,
        Modifier.width(240.dp).testTag("document"),
      )
    }
    fun bounds(text: String) = compose.onNodeWithText(text).fetchSemanticsNode().boundsInRoot
    fun baseline(text: String): Float {
      val result = mutableListOf<TextLayoutResult>()
      compose.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
        it(result)
      }
      return bounds(text).top + result.single().firstBaseline
    }
    val first = bounds("First")
    val second = bounds("Second")
    val deep = bounds("Deep")
    val tight = bounds("Tight")
    val suppressed = bounds("Suppressed")
    val outside = bounds("Outside")
    val inset = with(compose.density) { 15.dp.roundToPx() }
    val gutter = with(compose.density) { 24.sp.roundToPx() }
    val gap = with(compose.density) { 24.sp.roundToPx() }
    assertThat(tight.left - outside.left).isWithin(.5f).of((inset + gutter).toFloat())
    assertThat(first.left - tight.left).isWithin(.5f).of(inset.toFloat())
    assertThat(deep.left - first.left).isWithin(.5f).of(gutter.toFloat())
    assertThat(suppressed.left - tight.left).isWithin(.5f).of(gutter.toFloat())
    assertThat(second.top - first.bottom).isWithin(.5f).of(gap.toFloat())
    assertThat(deep.top - second.bottom).isWithin(.5f).of(gap.toFloat())
    assertThat(tight.top).isEqualTo(deep.bottom)
    assertThat(suppressed.top).isEqualTo(tight.bottom)
    assertThat(outside.top - suppressed.bottom).isWithin(.5f).of(gap.toFloat())
    val marker = compose.onAllNodesWithText("•", useUnmergedTree = true)[0]
    val markerResult = mutableListOf<TextLayoutResult>()
    marker.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(markerResult) }
    assertThat(marker.fetchSemanticsNode().boundsInRoot.top + markerResult.single().firstBaseline)
      .isWithin(1f)
      .of(baseline("First"))
    compose
      .onNodeWithText("First")
      .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    val textNodes =
      compose
        .onAllNodes(
          SemanticsMatcher.keyIsDefined(SemanticsProperties.Text),
          useUnmergedTree = true,
        )
        .fetchSemanticsNodes()
    assertThat(textNodes.flatMap { it.config[SemanticsProperties.Text] }.map { it.text })
      .containsExactly(
        "•",
        "First",
        "Second",
        "◦",
        "Deep",
        "•",
        "Tight",
        "◦",
        "Suppressed",
        "Outside",
      )
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
    val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
    val barWidth = with(compose.density) { 3.dp.toPx() }
    val innerX = inset + gutter
    for (y in 0 until suppressed.bottom.toInt()) {
      assertThat(pixels[0, y]).isEqualTo(Color.Red)
      assertThat(pixels[barWidth.toInt() - 1, y]).isEqualTo(Color.Red)
      assertThat(pixels[barWidth.toInt() + 1, y]).isNotEqualTo(Color.Red)
    }
    for (y in 0 until deep.bottom.toInt()) {
      assertThat(pixels[innerX, y]).isEqualTo(Color.Red)
      assertThat(pixels[innerX + barWidth.toInt() - 1, y]).isEqualTo(Color.Red)
      assertThat(pixels[innerX + barWidth.toInt() + 1, y]).isNotEqualTo(Color.Red)
    }
    for (y in deep.bottom.toInt() until pixels.height) {
      assertThat(pixels[innerX, y]).isNotEqualTo(Color.Red)
    }
    for (y in suppressed.bottom.toInt() until pixels.height) {
      assertThat(pixels[0, y]).isNotEqualTo(Color.Red)
    }
  }
}
