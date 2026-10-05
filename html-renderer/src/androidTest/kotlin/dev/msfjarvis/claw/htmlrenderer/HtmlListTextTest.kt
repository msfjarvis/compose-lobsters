/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
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

class HtmlListTextTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun changingHtmlWithUnchangedVisibleMarkerRowsStillMeasuresNewContent() {
    val html = mutableStateOf("<ul><li>A</li></ul>")
    compose.setContent { ListSubject(html.value) }
    compose.runOnIdle { html.value = "<ul><li>A</li>unmarked</ul>" }
    compose.onNodeWithText("unmarked").assertIsDisplayed()
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val extra = compose.onNodeWithText("unmarked").fetchSemanticsNode().boundsInRoot
    assertThat(extra.top).isWithin(.5f).of(a.bottom)
  }

  @Test
  fun separatorOnlyItemsDoNotCreateMarkersOrRestoreSuppressedParentMarkers() {
    compose.setContent {
      ListSubject("<ul><li><hr></li><li><hr><ul><li>nested</li></ul></li><li>ordinary</li></ul>")
    }
    compose.onAllNodesWithText("•", useUnmergedTree = true).assertCountEquals(1)
    compose.onAllNodesWithText("◦", useUnmergedTree = true).assertCountEquals(1)
    compose.onNodeWithText("nested").assertIsDisplayed()
    compose.onNodeWithText("ordinary").assertIsDisplayed()
  }

  @Test
  fun explicitSeparatorKeepsMarkerForOrdinaryItemAndItsParagraphSpacing() {
    compose.setContent { ListSubject("<ul><li>before<hr>after</li></ul>") }
    compose.onAllNodesWithText("•", useUnmergedTree = true).assertCountEquals(1)
    val before = compose.onNodeWithText("before").fetchSemanticsNode().boundsInRoot
    val after = compose.onNodeWithText("after").fetchSemanticsNode().boundsInRoot
    assertThat(after.top - before.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
  }

  @Test
  fun orderedMarkersShareWidenedGutterBaselineAndWrappedContentEdge() {
    val wrapped =
      "Wrapped continuation lines stay at the content edge without overlapping wide markers"
    val html =
      "<ol>" +
        (1..100).joinToString("") {
          when (it) {
            9 -> "<li>$wrapped</li>"
            10 -> "<li>ten</li>"
            100 -> "<li>hundred</li>"
            else -> "<li></li>"
          }
        } +
        "</ol>"
    compose.setContent { ListSubject(html, Modifier.width(220.dp)) }
    val a = compose.onNodeWithText(wrapped).fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("ten").fetchSemanticsNode().boundsInRoot
    val c = compose.onNodeWithText("hundred").fetchSemanticsNode().boundsInRoot
    val markers =
      listOf("9.", "10.", "100.").map {
        compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot
      }
    assertThat(a.left).isWithin(.5f).of(b.left)
    assertThat(b.left).isWithin(.5f).of(c.left)
    assertThat(markers.map { it.right }.distinct()).hasSize(1)
    assertThat(a.left - markers[2].right).isWithin(.5f).of(with(compose.density) { 8.dp.toPx() })
    assertThat(a.left).isAtLeast(with(compose.density) { 24.sp.toPx() })
    assertThat(b.top).isWithin(.5f).of(a.bottom)
    assertThat(c.top).isWithin(.5f).of(b.bottom)
    listOf("9." to wrapped, "10." to "ten", "100." to "hundred").forEach { (marker, content) ->
      assertThat(baseline(marker)).isWithin(1f).of(baseline(content))
    }
    val result = textLayout(wrapped)
    assertThat(result.lineCount).isGreaterThan(1)
    assertThat(result.getLineLeft(0)).isEqualTo(result.getLineLeft(1))
    assertThat(result.hasVisualOverflow).isFalse()
  }

  @Test
  fun markerUsesFirstDescendantBaselineWhenNestedListComesBeforeParentProse() {
    compose.setContent {
      ListSubject(
        "<ul><li><ul><li><h2>first descendant</h2></li></ul>after</li><li>last</li></ul>",
        Modifier.width(220.dp),
      )
    }
    val outerMarker = compose.onAllNodesWithText("•", useUnmergedTree = true)[0]
    val markerResults = mutableListOf<TextLayoutResult>()
    outerMarker.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(markerResults) }
    val markerBaseline =
      outerMarker.fetchSemanticsNode().boundsInRoot.top + markerResults.single().firstBaseline
    assertThat(markerBaseline).isWithin(1f).of(baseline("first descendant"))
    assertThat(baseline("◦")).isWithin(1f).of(baseline("first descendant"))
    val nested = compose.onNodeWithText("first descendant").fetchSemanticsNode().boundsInRoot
    val after = compose.onNodeWithText("after").fetchSemanticsNode().boundsInRoot
    assertThat(after.top).isWithin(.5f).of(nested.bottom)
  }

  @Test
  fun distinctParagraphsHaveOneGapButFollowingSiblingHasNone() {
    compose.setContent { ListSubject("<ul><li><p>A</p><p>B</p></li><li><p>C</p></li></ul>") }
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    val c = compose.onNodeWithText("C").fetchSemanticsNode().boundsInRoot
    assertThat(b.top - a.bottom).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
    assertThat(c.top).isWithin(.5f).of(b.bottom)
    compose.onAllNodesWithText("•", useUnmergedTree = true).assertCountEquals(2)
  }

  @Test
  fun rtlGutterAndNestedIndentRemainLogicalAcrossFontScaleChanges() {
    val scale = mutableStateOf(1f)
    var gutter = 0f
    var gap = 0f
    compose.setContent {
      val density = Density(LocalDensity.current.density, scale.value)
      gutter = with(density) { 24.sp.toPx() }
      gap = with(density) { 8.dp.toPx() }
      CompositionLocalProvider(
        LocalDensity provides density,
        LocalLayoutDirection provides LayoutDirection.Rtl,
      ) {
        ListSubject(
          "<ul><li>日本語 العربية</li><li><ul><li>nested</li></ul></li><li>final</li></ul>",
          Modifier.width(220.dp),
        )
      }
    }
    fun check() {
      val first = compose.onNodeWithText("日本語 العربية").fetchSemanticsNode().boundsInRoot
      val nested = compose.onNodeWithText("nested").fetchSemanticsNode().boundsInRoot
      val last =
        compose.onNodeWithText("final").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
      val marker =
        compose.onAllNodesWithText("•", useUnmergedTree = true)[0].fetchSemanticsNode().boundsInRoot
      assertThat(marker.left - first.right).isWithin(.5f).of(gap)
      assertThat(first.right - nested.right).isWithin(1f).of(gutter)
      assertThat(nested.top).isWithin(.5f).of(first.bottom)
      assertThat(last.top).isWithin(.5f).of(nested.bottom)
      assertThat(textLayout("日本語 العربية").hasVisualOverflow).isFalse()
      assertThat(textLayout("final").hasVisualOverflow).isFalse()
    }
    check()
    compose.runOnIdle { scale.value = 2f }
    check()
  }

  @Test
  fun intrinsicWidthAndHeightPreserveGuttersFullContentAndBothBaselines() {
    val intrinsic = mutableStateOf(IntrinsicSize.Min)
    var firstBaseline = 0
    var lastBaseline = 0
    compose.setContent {
      ListSubject(
        "<ul><li>first</li><li><ul><li>last</li></ul></li></ul>",
        Modifier.width(intrinsic.value).height(intrinsic.value).testTag("document").layout {
          measurable,
          constraints ->
          val placeable = measurable.measure(constraints)
          firstBaseline = placeable[FirstBaseline]
          lastBaseline = placeable[LastBaseline]
          layout(placeable.width, placeable.height) { placeable.place(0, 0) }
        },
      )
    }
    fun check() {
      val root = compose.onNodeWithTag("document").fetchSemanticsNode().boundsInRoot
      val last = compose.onNodeWithText("last").fetchSemanticsNode().boundsInRoot
      assertThat(root.top + firstBaseline).isWithin(1f).of(baseline("first"))
      assertThat(root.top + lastBaseline).isWithin(1f).of(baseline("last", first = false))
      assertThat(root.bottom).isWithin(.5f).of(last.bottom)
      assertThat(textLayout("last").hasVisualOverflow).isFalse()
      assertThat(last.left).isWithin(1f).of(with(compose.density) { 48.sp.toPx() })
    }
    check()
    compose.runOnIdle { intrinsic.value = IntrinsicSize.Max }
    check()
  }

  @Test
  fun subGutterWidthNeverCreatesNegativeConstraintsOrDropsTextSemantics() {
    compose.setContent {
      ListSubject("<ol><li>x<ul><li>deep</li></ul></li></ol>", Modifier.width(1.dp))
    }
    listOf("x", "deep", "1.", "◦").forEach {
      val bounds = compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot
      assertThat(bounds.width).isAtLeast(0f)
      assertThat(bounds.width).isAtMost(with(compose.density) { 1.dp.toPx() })
    }
  }

  @Test
  fun listLinksStayIndividuallyActionableAndQuoteNestingDoesNotChangeBulletDepth() {
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
        ListSubject(
          "<blockquote><ul><li>before <a href='source'>link</a></li><li><blockquote><ul><li>nested</li></ul></blockquote></li></ul></blockquote>",
          transform = { "final:$it" },
        )
      }
    }
    compose.onAllNodesWithText("•", useUnmergedTree = true).assertCountEquals(2)
    compose.onAllNodesWithText("◦", useUnmergedTree = true).assertCountEquals(1)
    compose.onNode(hasClickAction(), useUnmergedTree = true).performClick()
    assertThat(opened).containsExactly("final:source")
  }

  private fun textLayout(text: String): TextLayoutResult {
    val results = mutableListOf<TextLayoutResult>()
    compose.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
      it(results)
    }
    return results.single()
  }

  private fun baseline(text: String, first: Boolean = true): Float =
    compose.onNodeWithText(text).fetchSemanticsNode().boundsInRoot.top +
      textLayout(text).let { if (first) it.firstBaseline else it.lastBaseline }

  @Test
  fun listTextAndMarkersKeepSourceOrderInTheUnmergedSemanticTree() {
    compose.setContent {
      HtmlText(
        "<ol><li>first</li><li>second</li></ol>",
        TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        Color.Black,
        TextLinkStyles(),
        Color.Gray,
      )
    }
    val text =
      compose
        .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text), useUnmergedTree = true)
        .fetchSemanticsNodes()
        .flatMap { it.config[SemanticsProperties.Text] }
        .map { it.text }
    assertThat(text).containsExactly("1.", "first", "2.", "second").inOrder()
  }

  @Test
  fun simpleParagraphItemsAndListOnlyParentsHaveNoExtraRowsOrMarkers() {
    compose.setContent {
      HtmlText(
        "<ul><li><p>A</p></li><li><p>B</p></li><li><ul><li>C</li></ul></li></ul>",
        style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
        color = Color.Black,
        linkStyles = TextLinkStyles(),
        quoteBarColor = Color.Gray,
        modifier = Modifier.width(200.dp),
      )
    }
    compose.onAllNodesWithText("•", useUnmergedTree = true).assertCountEquals(2)
    compose.onAllNodesWithText("◦", useUnmergedTree = true).assertCountEquals(1)
    val a = compose.onNodeWithText("A").fetchSemanticsNode().boundsInRoot
    val b = compose.onNodeWithText("B").fetchSemanticsNode().boundsInRoot
    val c = compose.onNodeWithText("C").fetchSemanticsNode().boundsInRoot
    assertThat(b.top).isWithin(.5f).of(a.bottom)
    assertThat(c.top).isWithin(.5f).of(b.bottom)
    assertThat(a.left).isWithin(.5f).of(b.left)
    assertThat(c.left - b.left).isWithin(.5f).of(with(compose.density) { 24.sp.toPx() })
  }
}

@Composable
private fun ListSubject(
  html: String,
  modifier: Modifier = Modifier,
  transform: (String) -> String = { it },
) {
  HtmlText(
    html,
    TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    Color.Black,
    TextLinkStyles(),
    Color.Gray,
    modifier = modifier,
    transformDestination = transform,
  )
}
