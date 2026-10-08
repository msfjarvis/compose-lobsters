/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.common.BuildConfig
import org.junit.Rule
import org.junit.Test

class IntegratedRichTextTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun unchangedMixedHtmlRemeasuresAndRecolorsAcrossRuntimeThemeDirectionAndFontScaleMatrix() {
    val dark = mutableStateOf(false)
    val direction = mutableStateOf(LayoutDirection.Ltr)
    val scale = mutableStateOf(1f)
    val label = "https://example.com/long/path/日本語/🙂/العربية/end"
    compose.setContent {
      val colors = if (dark.value) darkColorScheme() else lightColorScheme()
      CompositionLocalProvider(
        LocalDensity provides Density(LocalDensity.current.density, scale.value),
        LocalLayoutDirection provides direction.value,
      ) {
        MaterialTheme(colorScheme = colors) {
          Column(Modifier.width(220.dp).verticalScroll(rememberScrollState())) {
            ThemedRichText(
              """<blockquote><h2>Heading</h2><ul><li><a href="https://example.com/long/path">$label</a></li>
              <li>日本語 🙂 العربية</li></ul></blockquote><p>Final line</p>""",
              Modifier.background(colors.background).testTag("document"),
            )
          }
        }
      }
    }
    for (fontScale in listOf(1f, 2f)) for (rtl in listOf(false, true)) for (night in
      listOf(false, true)) {
      compose.runOnIdle {
        dark.value = night
        direction.value = if (rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
        scale.value = fontScale
      }
      compose.onNodeWithText("Heading").performScrollTo()
      val colors = if (night) darkColorScheme() else lightColorScheme()
      val heading = layout("Heading")
      assertThat(heading.layoutInput.style.color).isEqualTo(colors.onBackground)
      assertThat(heading.layoutInput.density.fontScale).isEqualTo(fontScale)
      assertThat(heading.layoutInput.layoutDirection).isEqualTo(direction.value)
      val linkText = layout(label).layoutInput.text
      val link = linkText.getLinkAnnotations(0, linkText.length).single().item as LinkAnnotation.Url
      assertThat(link.url).isEqualTo("https://example.com/long/path")
      assertThat(link.styles?.style?.color).isEqualTo(colors.onSurface)
      assertThat(link.styles?.style?.background).isEqualTo(colors.surfaceVariant)
      val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
      val title = compose.onNodeWithText("Heading").fetchSemanticsNode().boundsInRoot
      val inset = with(compose.density) { 15.dp.toPx() }
      assertThat(if (rtl) root.right - title.right else title.left - root.left)
        .isWithin(.5f)
        .of(inset)
      val pixels = compose.onNodeWithTag("document").captureToImage().toPixelMap()
      assertThat(pixels[if (rtl) pixels.width - 1 else 0, 0]).isEqualTo(colors.outlineVariant)
      for (text in listOf(label, "日本語 🙂 العربية", "Final line")) {
        val result = layout(text)
        assertThat(result.hasVisualOverflow).isFalse()
        assertThat(result.getLineEnd(result.lineCount - 1)).isEqualTo(text.length)
      }
      assertThat(layout(label).lineCount).isGreaterThan(1)
      compose.onNodeWithText("Final line").performScrollTo().assertIsDisplayed()
    }
  }

  @Test
  fun separateLinksAcrossContainersActivateOnceByPointerAccessibilityAndKeyboardInSourceOrder() {
    val opened = mutableListOf<String>()
    lateinit var input: InputModeManager
    val start = FocusRequester()
    compose.setContent {
      input = LocalInputModeManager.current
      MaterialTheme {
        CompositionLocalProvider(
          LocalUriHandler provides
            object : UriHandler {
              override fun openUri(uri: String) {
                opened += uri
              }
            }
        ) {
          Column {
            Text("Keyboard start", Modifier.focusRequester(start).focusable())
            ThemedRichText(
              """<blockquote><ul><li><a href="https://lobste.rs/s/abc123/title">Story</a></li></ul></blockquote>
              <ul><li><blockquote><a href="https://example.com/?a=1&amp;b=2">External</a></blockquote></li></ul>"""
            )
          }
        }
      }
    }
    val links = compose.onAllNodes(hasClickAction(), useUnmergedTree = true)
    assertThat(links.fetchSemanticsNodes()).hasSize(2)
    val expected =
      listOf("${BuildConfig.DEEPLINK_SCHEME}://comments/abc123", "https://example.com/?a=1&b=2")
    // Establish a host target: entering keyboard mode can auto-focus an arbitrary descendant.
    compose.runOnIdle {
      assertThat(input.requestInputMode(InputMode.Keyboard)).isTrue()
      start.requestFocus()
    }
    for (index in 0..1) {
      compose.onRoot().performKeyInput { pressKey(Key.Tab) }
      assertThat(
          links.fetchSemanticsNodes().map { it.config.getOrNull(SemanticsProperties.Focused) }
        )
        .isEqualTo(if (index == 0) listOf(true, false) else listOf(false, true))
      links[index].assertIsFocused().performKeyInput { pressKey(Key.Enter) }
      assertThat(opened).containsExactlyElementsIn(expected.take(index + 1)).inOrder()
    }
    for (index in 0..1) {
      links[index].performTouchInput { click(center) }
      assertThat(opened).containsExactlyElementsIn(expected + expected.take(index + 1)).inOrder()
    }
    for (index in 0..1) {
      // Native OnClick is the accessibility activation seam; this is not manual TalkBack evidence.
      links[index].performClick()
      assertThat(opened)
        .containsExactlyElementsIn(expected + expected + expected.take(index + 1))
        .inOrder()
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
