/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.common.BuildConfig
import dev.msfjarvis.claw.model.Comment
import kotlin.time.Instant
import org.junit.Rule
import org.junit.Test

class CommentRichTextInteractionTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun nestedCommentLinksDoNotToggleTrayButOrdinaryTapsLongPressAndCollapsedTapsRetainRowBehavior() {
    val expanded = mutableStateOf(true)
    val scale = mutableStateOf(1f)
    val toggles = mutableListOf<Pair<String, Boolean>>()
    val opened = mutableListOf<String>()
    val node =
      CommentNode(
        comment =
          Comment(
            shortId = "fixture",
            comment =
              """<blockquote><ul>
        <li><a href="https://lobste.rs/s/abc123/title">Story</a></li><li>Ordinary</li>
        </ul></blockquote><p>日本語 🙂 العربية end</p>""",
            score = 7,
            timestamp = Instant.parse("2020-01-01T00:00:00Z"),
            edited = false,
            parentComment = "parent",
            user = "Alice",
            isUpvoted = false,
          ),
        isPostAuthor = false,
        indentLevel = 3,
      )
    compose.setContent {
      CompositionLocalProvider(
        LocalDensity provides Density(LocalDensity.current.density, scale.value),
        LocalUriHandler provides
          object : UriHandler {
            override fun openUri(uri: String) {
              opened += uri
            }
          },
      ) {
        MaterialTheme {
          Column(Modifier.width(240.dp).verticalScroll(rememberScrollState())) {
            CommentEntry(
              expanded.value,
              node,
              openUserProfile = {},
              onToggleExpandedState = { id, value ->
                toggles += id to value
                expanded.value = value
              },
              isLoggedIn = false,
              upvoteComment = {},
              unvoteComment = {},
              onReply = { _, _ -> },
              onShare = {},
            )
          }
        }
      }
    }
    compose.onNodeWithContentDescription("Share").assertDoesNotExist()
    val link =
      compose
        .onNodeWithText("Story", useUnmergedTree = true)
        .onChildren()
        .filter(hasClickAction())[0]
    link.performTouchInput { click(center) }
    assertThat(opened).containsExactly("${BuildConfig.DEEPLINK_SCHEME}://comments/abc123")
    compose.onNodeWithContentDescription("Share").assertDoesNotExist()
    compose
      .onNodeWithText("Ordinary", useUnmergedTree = true)
      .performScrollTo()
      .assertIsDisplayed()
      .performTouchInput { click(center) }
    assertThat(opened).hasSize(1)
    compose.onNodeWithContentDescription("Share").performScrollTo().assertIsDisplayed()
    assertThat(toggles).isEmpty()
    compose.runOnIdle { scale.value = 2f }
    val final = compose.onNodeWithText("日本語 🙂 العربية end", useUnmergedTree = true)
    final.performScrollTo().assertIsDisplayed()
    val results = mutableListOf<TextLayoutResult>()
    final.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
    assertThat(results.single().hasVisualOverflow).isFalse()
    assertThat(results.single().lineCount).isGreaterThan(1)
    assertThat(results.single().getLineEnd(results.single().lineCount - 1))
      .isEqualTo("日本語 🙂 العربية end".length)
    compose.onNodeWithText("Ordinary", useUnmergedTree = true).performScrollTo().performTouchInput {
      longClick(center)
    }
    assertThat(toggles).containsExactly("fixture" to false)
    compose.onNodeWithText("Ordinary", useUnmergedTree = true).assertDoesNotExist()
    compose.onNodeWithContentDescription("Share").assertDoesNotExist()
    compose.onNodeWithText("7", useUnmergedTree = true).performTouchInput { click(center) }
    assertThat(toggles).containsExactly("fixture" to false, "fixture" to true).inOrder()
    compose.onNodeWithText("Ordinary", useUnmergedTree = true).assertIsDisplayed()
    compose.onNodeWithContentDescription("Share").assertDoesNotExist()
    assertThat(opened).hasSize(1)
  }
}
