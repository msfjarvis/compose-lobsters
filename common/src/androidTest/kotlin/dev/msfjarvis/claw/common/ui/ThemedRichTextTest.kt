/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.common.BuildConfig
import org.junit.Rule
import org.junit.Test

class ThemedRichTextTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun oneFinalLinkReachesTextAndThemeChangesRefreshUnchangedHtml() {
    val colors = mutableStateOf(lightColorScheme())
    compose.setContent {
      MaterialTheme(colorScheme = colors.value) {
        ThemedRichText("<a href='https://lobste.rs/s/abc123/title'>open</a>")
      }
    }
    fun link(): LinkAnnotation.Url {
      val text =
        compose
          .onNodeWithText("open", useUnmergedTree = true)
          .fetchSemanticsNode()
          .config[SemanticsProperties.Text]
          .single()
      val links = text.getLinkAnnotations(0, text.length)
      assertThat(links).hasSize(1)
      return links.single().item as LinkAnnotation.Url
    }
    assertThat(link().url).isEqualTo("${BuildConfig.DEEPLINK_SCHEME}://comments/abc123")
    assertThat(link().styles?.style?.fontWeight).isEqualTo(FontWeight.Bold)
    assertThat(link().styles?.style?.textDecoration).isEqualTo(TextDecoration.Underline)
    compose.runOnIdle {
      colors.value = colors.value.copy(onSurface = Color.Red, surfaceVariant = Color.Blue)
    }
    assertThat(link().styles?.style?.color).isEqualTo(colors.value.onSurface)
    assertThat(link().styles?.style?.background).isEqualTo(colors.value.surfaceVariant)
  }

  @Test
  fun currentForegroundTypographyAndQuoteBarReachNativeTextAndDrawing() {
    val colors =
      mutableStateOf(
        lightColorScheme(
          background = Color.White,
          onBackground = Color.Magenta,
          outlineVariant = Color.Red,
        )
      )
    val body = TextStyle(fontSize = 17.sp, lineHeight = 26.sp)
    compose.setContent {
      MaterialTheme(colorScheme = colors.value, typography = Typography(bodyLarge = body)) {
        ThemedRichText(
          "<blockquote>Quote</blockquote><p>After</p>",
          Modifier.width(180.dp).background(colors.value.background).testTag("document"),
        )
      }
    }
    for (updated in listOf(false, true)) {
      if (updated)
        compose.runOnIdle {
          colors.value = colors.value.copy(outlineVariant = Color.Blue, onBackground = Color.Green)
        }
      val result = layout("Quote")
      assertThat(result.layoutInput.style.fontSize).isEqualTo(body.fontSize)
      assertThat(result.layoutInput.style.lineHeight).isEqualTo(body.lineHeight)
      assertThat(result.layoutInput.style.lineBreak).isEqualTo(LineBreak.Paragraph)
      assertThat(result.layoutInput.style.color).isEqualTo(colors.value.onBackground)
      val quote =
        compose.onNodeWithText("Quote", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
      val after =
        compose.onNodeWithText("After", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
      assertThat(quote.left - after.left).isWithin(.5f).of(with(compose.density) { 15.dp.toPx() })
      val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
      val width = with(compose.density) { 3.dp.roundToPx() }
      for (y in 0 until quote.bottom.toInt()) for (x in 0 until width) {
        assertThat(pixels[x, y]).isEqualTo(colors.value.outlineVariant)
      }
      assertThat(pixels[width, 0]).isNotEqualTo(colors.value.outlineVariant)
      for (y in quote.bottom.toInt() until after.top.toInt()) {
        assertThat(pixels[0, y]).isNotEqualTo(colors.value.outlineVariant)
      }
    }
  }

  @Test
  fun callerPaddingAndClickBehaviorSurroundFullSoftWrappedContent() {
    var rowClicks = 0
    val last =
      "A long final paragraph that must wrap onto several lines without losing its final words."
    compose.setContent {
      MaterialTheme {
        Column {
          ThemedRichText(
            "<p>First</p><ul><li><p>A</p></li><li><p>B</p></li></ul><p>$last</p>",
            Modifier.width(180.dp)
              .clickable { rowClicks++ }
              .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
              .testTag("document"),
          )
          Text("Footer")
        }
      }
    }
    val root =
      compose.onNodeWithTag("document", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
    val first =
      compose.onNodeWithText("First", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
    val a = compose.onNodeWithText("A", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
    val end =
      compose
        .onNodeWithText(last, useUnmergedTree = true)
        .assertIsDisplayed()
        .fetchSemanticsNode()
        .boundsInRoot
    val footer = compose.onNodeWithText("Footer").fetchSemanticsNode().boundsInRoot
    // Native root semantics include caller-owned padding, even with testTag after padding.
    assertThat(root.width).isWithin(.5f).of(with(compose.density) { 180.dp.toPx() })
    assertThat(first.left).isWithin(.5f).of(with(compose.density) { 16.dp.toPx() })
    assertThat(a.bottom).isEqualTo(b.top)
    assertThat(root.bottom - end.bottom).isWithin(.5f).of(with(compose.density) { 12.dp.toPx() })
    assertThat(footer.top).isEqualTo(root.bottom)
    assertThat(footer.top - end.bottom).isWithin(.5f).of(with(compose.density) { 12.dp.toPx() })
    assertThat(layout(last).lineCount).isGreaterThan(1)
    assertThat(layout(last).getLineEnd(layout(last).lineCount - 1)).isEqualTo(last.length)
    assertThat(layout(last).hasVisualOverflow).isFalse()
    compose.onNodeWithText("First", useUnmergedTree = true).performTouchInput { click(center) }
    assertThat(rowClicks).isEqualTo(1)
  }

  @Test
  fun eachSourceAnchorActivatesOnceWithItsFinalDestinationAndInertContentDoesNotAct() {
    val opened = mutableListOf<String>()
    compose.setContent {
      MaterialTheme {
        CompositionLocalProvider(
          LocalUriHandler provides
            object : UriHandler {
              override fun openUri(uri: String) {
                opened += uri
              }
            }
        ) {
          ThemedRichText(
            """<p><a href="https://lobste.rs/s/abc123/title">Story</a></p>
            <p><a href="https://example.com/page?q=1&amp;x=2#end">External</a></p>
            <script>not rendered</script><img src="https://example.com/image.png">
            <p>Ordinary</p>"""
          )
        }
      }
    }
    assertThat(opened).isEmpty()
    compose.onNodeWithText("not rendered").assertDoesNotExist()
    val links = compose.onAllNodes(hasClickAction(), useUnmergedTree = true)
    assertThat(links.fetchSemanticsNodes()).hasSize(2)
    links[0].performTouchInput { click(center) }
    assertThat(opened).containsExactly("${BuildConfig.DEEPLINK_SCHEME}://comments/abc123")
    links[1].performTouchInput { click(center) }
    assertThat(opened)
      .containsExactly(
        "${BuildConfig.DEEPLINK_SCHEME}://comments/abc123",
        "https://example.com/page?q=1&x=2#end",
      )
      .inOrder()
    compose.onNodeWithText("Ordinary").performTouchInput { click(center) }
    assertThat(opened).hasSize(2)
    for (label in listOf("Story", "External")) {
      val text =
        compose
          .onNodeWithText(label, useUnmergedTree = true)
          .fetchSemanticsNode()
          .config[SemanticsProperties.Text]
          .single()
      assertThat(text.getLinkAnnotations(0, text.length)).hasSize(1)
    }
  }

  private fun layout(text: String): TextLayoutResult {
    val results = mutableListOf<TextLayoutResult>()
    compose.onNodeWithText(text, useUnmergedTree = true).performSemanticsAction(
      SemanticsActions.GetTextLayoutResult
    ) {
      it(results)
    }
    return results.single()
  }
}
