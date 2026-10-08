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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class HtmlTextTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun nativeLinksActivateByPointerSemanticsAndKeyboardWithOneFinalDestination() {
    val opened = mutableListOf<String>()
    lateinit var inputModeManager: InputModeManager
    compose.setContent {
      inputModeManager = LocalInputModeManager.current
      CompositionLocalProvider(
        LocalUriHandler provides
          object : UriHandler {
            override fun openUri(uri: String) {
              opened += uri
            }
          }
      ) {
        Subject(
          "<p>Before <a href='original'><b>open</b></a> after</p>",
          transform = { "final:$it" },
        )
      }
    }
    val link = compose.onNode(hasClickAction(), useUnmergedTree = true)
    link.performTouchInput { click(center) }
    link.performClick()
    compose.runOnIdle { assertThat(inputModeManager.requestInputMode(InputMode.Keyboard)).isTrue() }
    compose.onRoot().performKeyInput { pressKey(Key.Tab) }
    link.assertIsFocused()
    link.performKeyInput { pressKey(Key.Enter) }
    assertThat(opened).containsExactly("final:original", "final:original", "final:original")
  }

  @Test
  fun ordinaryTextTapReachesParentRatherThanAnInventedDocumentClickTarget() {
    var clicks = 0
    compose.setContent { Subject("<p>ordinary</p>", Modifier.clickable { clicks++ }) }
    compose.onNodeWithText("ordinary").performTouchInput { click(center) }
    assertThat(clicks).isEqualTo(1)
    assertThat(compose.onAllNodes(hasClickAction(), useUnmergedTree = true).fetchSemanticsNodes())
      .hasSize(1)
  }

  @Test
  fun unchangedHtmlUpdatesLinkStylesAndTransformWithoutRebuildingAnnotationsForTypography() {
    val linkColor = mutableStateOf(Color.Red)
    val foreground = mutableStateOf(Color.Black)
    var annotations = 0
    val typography = mutableStateOf(TextStyle(fontSize = 16.sp, lineHeight = 24.sp))
    val destination =
      mutableStateOf<(String) -> String>({
        annotations++
        "first:$it"
      })
    compose.setContent {
      Subject(
        "<a href='source'>link</a>",
        styles = TextLinkStyles(SpanStyle(color = linkColor.value)),
        color = foreground.value,
        transform = destination.value,
        style = typography.value,
      )
    }
    fun annotation(): LinkAnnotation.Url {
      val text =
        compose
          .onNodeWithText("link")
          .fetchSemanticsNode()
          .config[SemanticsProperties.Text]
          .single()
      return text.getLinkAnnotations(0, text.length).single().item as LinkAnnotation.Url
    }
    assertThat(annotation().styles?.style?.color).isEqualTo(Color.Red)
    assertThat(annotations).isEqualTo(1)
    compose.runOnIdle {
      foreground.value = Color.White
      typography.value = TextStyle(fontSize = 18.sp, lineHeight = 28.sp)
    }
    compose.waitForIdle()
    assertThat(annotations).isEqualTo(1)
    compose.runOnIdle { linkColor.value = Color.Blue }
    assertThat(annotation().styles?.style?.color).isEqualTo(Color.Blue)
    assertThat(annotations).isEqualTo(2)
    compose.runOnIdle {
      destination.value = {
        annotations++
        "second:$it"
      }
    }
    assertThat(annotation().url).isEqualTo("second:source")
  }

  @Test
  fun intrinsicContainerForwardsFirstBaselineAcrossBlocksAndDefinitionInset() {
    compose.setContent {
      Row(Modifier.width(IntrinsicSize.Min)) {
        Subject("<dl><dd>A</dd><dt>B</dt></dl>", Modifier.alignByBaseline())
        Text(
          "reference",
          Modifier.alignByBaseline(),
          style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        )
      }
    }
    fun baseline(text: String): Float {
      val results = mutableListOf<TextLayoutResult>()
      val node = compose.onNodeWithText(text)
      node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
      return node.fetchSemanticsNode().boundsInRoot.top + results.single().firstBaseline
    }
    assertThat(baseline("A")).isWithin(.5f).of(baseline("reference"))
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(a.left - b.left).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun narrowLargeFontContentWrapsWithoutLosingFinalLinesAndGapsScaleWithDensity() {
    var expectedGap = 0f
    compose.setContent {
      val scaledDensity = Density(LocalDensity.current.density, 2f)
      // Android's font scaling is nonlinear; use the active conversion, not a scalar multiplier.
      expectedGap = with(scaledDensity) { 24.sp.toPx() }
      CompositionLocalProvider(LocalDensity provides scaledDensity) {
        Subject("<p>日本語 🙂 mixed العربية</p><p>final line</p>", Modifier.width(120.dp))
      }
    }
    val first = compose.onNodeWithText("日本語 🙂 mixed العربية").fetchSemanticsNode().boundsInRoot
    val last =
      compose.onNodeWithText("final line").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
    assertThat(last.top - first.bottom).isWithin(.5f).of(expectedGap)
    val results = mutableListOf<TextLayoutResult>()
    compose.onNodeWithText("final line").performSemanticsAction(
      SemanticsActions.GetTextLayoutResult
    ) {
      it(results)
    }
    assertThat(results.single().getLineEnd(results.single().lineCount - 1)).isEqualTo(10)
    assertThat(results.single().hasVisualOverflow).isFalse()
  }

  @Test
  fun definitionDescriptionIntrinsicHeightIncludesItsParagraphGapOnlyOnce() {
    compose.setContent {
      Subject(
        "<dl><dd><p>A</p><p>B</p></dd></dl>",
        Modifier.width(200.dp).height(IntrinsicSize.Min).testTag("document"),
      )
    }
    val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(b.top - a.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
    assertThat(root.bottom).isWithin(.5f).of(b.bottom)
  }

  @Test
  fun intrinsicHeightAndNestedContainersForwardBothDescendantBaselines() {
    var firstBaseline = 0
    var lastBaseline = 0
    compose.setContent {
      Subject(
        "<blockquote><h2>A</h2><p>Nested wrapped paragraph</p></blockquote><p>last</p>",
        Modifier.width(140.dp).height(IntrinsicSize.Min).testTag("document").layout {
          measurable,
          constraints ->
          val placeable = measurable.measure(constraints)
          firstBaseline = placeable[FirstBaseline]
          lastBaseline = placeable[LastBaseline]
          layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        },
      )
    }
    fun baseline(text: String, first: Boolean): Float {
      val results = mutableListOf<TextLayoutResult>()
      val node = compose.onNodeWithText(text)
      node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
      return node.fetchSemanticsNode().boundsInRoot.top +
        if (first) results.single().firstBaseline else results.single().lastBaseline
    }
    assertThat(firstBaseline.toFloat()).isWithin(.5f).of(baseline("A", true))
    assertThat(lastBaseline.toFloat()).isWithin(.5f).of(baseline("last", false))
    val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
    val last = compose.onNodeWithText("last").fetchSemanticsNode().boundsInRoot
    assertThat(root.bottom).isWithin(.5f).of(last.bottom)
  }

  @Test
  fun subInsetWidthDoesNotProduceNegativeConstraints() {
    compose.setContent { Subject("<dl><dd>x</dd></dl>", Modifier.width(1.dp)) }
    val bounds = compose.onNodeWithText("x").fetchSemanticsNode().boundsInRoot
    assertThat(bounds.width).isAtLeast(0f)
    assertThat(bounds.width).isAtMost(with(compose.density) { 1.dp.toPx() })
  }

  @Test
  fun rtlDefinitionIndentIsLogicalAndNarrowConstraintsRemainSafe() {
    compose.setContent {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Subject("<dl><dt>Term</dt><dd>Description</dd></dl>", Modifier.width(160.dp))
      }
    }
    val term = compose.onNodeWithText("Term").fetchSemanticsNode().boundsInRoot
    val description = compose.onNodeWithText("Description").fetchSemanticsNode().boundsInRoot
    assertThat(term.right - description.right)
      .isWithin(.5f)
      .of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun paragraphWrappedDefinitionDescriptionKeepsItsLeadingIndent() {
    compose.setContent { Subject("<dl><dt>Term</dt><dd><p>Description</p></dd></dl>") }
    val term = compose.onNodeWithText("Term").fetchSemanticsNode().boundsInRoot
    val description = compose.onNodeWithText("Description").fetchSemanticsNode().boundsInRoot
    assertThat(description.left - term.left)
      .isWithin(.5f)
      .of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun definitionIndentContainsParagraphsNestedListsAndQuotes() {
    compose.setContent {
      Subject(
        "<dl><dt>Term</dt><dd><p>Description</p><ul><li>Nested</li></ul><blockquote><p>Quoted</p></blockquote></dd></dl>"
      )
    }
    val term = compose.onNodeWithText("Term").fetchSemanticsNode().boundsInRoot
    val description = compose.onNodeWithText("Description").fetchSemanticsNode().boundsInRoot
    val nested = compose.onNodeWithText("Nested").fetchSemanticsNode().boundsInRoot
    val quoted = compose.onNodeWithText("Quoted").fetchSemanticsNode().boundsInRoot
    val definitionIndent = with(compose.density) { 24.sp.toPx() }
    assertThat(description.left - term.left).isWithin(.5f).of(definitionIndent)
    assertThat(nested.left - term.left)
      .isWithin(.5f)
      .of(definitionIndent + with(compose.density) { 24.sp.toPx() })
    assertThat(quoted.left - term.left)
      .isWithin(.5f)
      .of(definitionIndent + with(compose.density) { 15.dp.toPx() })
  }

  @Test
  fun hrAtEndOfGenericWrapperSeparatesFollowingSibling() {
    compose.setContent { Subject("<div>A<hr></div>B") }
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(b.top - a.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun separatorOnlyGenericWrapperSeparatesItsSiblings() {
    compose.setContent { Subject("A<div><hr></div>B") }
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(b.top - a.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun paragraphsHaveOneMaxCombinedGapAndHeadingsKeepSemantics() {
    compose.setContent {
      HtmlText(
        "<hr><p>A</p><p>B</p><h1>Heading</h1><hr>",
        style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        color = Color.Black,
        linkStyles = TextLinkStyles(),
        quoteBarColor = Color.Gray,
      )
    }
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    assertThat(b.top - a.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
    assertThat(a.top).isEqualTo(0f)
    compose
      .onNodeWithText("Heading")
      .assertIsDisplayed()
      .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
  }
}

@Composable
private fun Subject(
  html: String,
  modifier: Modifier = Modifier,
  styles: TextLinkStyles = TextLinkStyles(),
  color: Color = Color.Black,
  transform: (String) -> String = { it },
  style: TextStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
) {
  HtmlText(
    html,
    style = style,
    color = color,
    linkStyles = styles,
    quoteBarColor = Color.Gray,
    modifier = modifier,
    transformDestination = transform,
  )
}
