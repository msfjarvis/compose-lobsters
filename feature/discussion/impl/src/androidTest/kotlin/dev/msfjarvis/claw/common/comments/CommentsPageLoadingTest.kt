/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.android.ui.navigation.CommentsHeaderData
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.core.NetworkState
import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

private data class OpenedPost(val postId: String, val postUrl: String, val commentsUrl: String)

class CommentsPageLoadingTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun headerRemainsVisibleWhileLoadingAndAfterDetailsArrive() {
    val header =
      CommentsHeaderData(
        "id",
        "https://lobste.rs/s/id",
        "List title",
        listOf("kotlin"),
        "https://example.com",
        "alice",
        false,
      )
    val details =
      UIPost(
        discussion = Discussion(listOf(SourceStory("id", "", "API title", "", "bob", emptyList()))),
        description = "Loaded description",
        commentCount = 0,
        commentsUrl = "https://lobste.rs/s/id",
      )
    val displayedHeader = mutableStateOf<CommentsHeaderData?>(null)
    val state = mutableStateOf<NetworkState>(NetworkState.Loading)
    val nodes = MutableStateFlow<List<CommentNode>>(emptyList())
    var openedPost: OpenedPost? = null
    val actions =
      object : PostActions {
        override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {
          openedPost = OpenedPost(postId, postUrl, commentsUrl)
        }

        override fun viewComments(post: UIPost) = Unit

        override fun viewCommentsPage(post: UIPost) = Unit

        override fun toggleSave(post: UIPost) = Unit

        override fun share(post: UIPost) = Unit

        override fun shareComment(commentId: String) = Unit

        override fun isPostRead(post: UIPost) = false

        override fun isPostSaved(post: UIPost) = false

        override suspend fun getLinkMetadata(url: String) = LinkMetadata(url, null)
      }
    compose.setContent {
      MaterialTheme {
        CommentsPageInternal(
          header = displayedHeader.value,
          networkState = state.value,
          postActions = actions,
          seenCommentsState = SeenCommentsState.NoBaseline,
          markSeenComments = { _, _ -> },
          openUserProfile = {},
          contentPadding = PaddingValues(),
          commentListState = rememberLazyListState(),
          commentNodes = nodes,
          createCommentNodes = { _, _ -> },
          updateCommentNodeExpanded = { _, _ -> },
          updateUnreadStatus = {},
          isLoggedIn = false,
          upvoteComment = {},
          unvoteComment = {},
          openReplyScreen = { _, _ -> },
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
    val screen = compose.onRoot().getUnclippedBoundsInRoot()
    val progress =
      compose
        .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
        .getUnclippedBoundsInRoot()
    assertThat((progress.top.value + progress.bottom.value) / 2)
      .isWithin(2.dp.value)
      .of((screen.top.value + screen.bottom.value) / 2)
    compose.runOnIdle { displayedHeader.value = header }
    compose.onNodeWithText("List title").assertIsDisplayed()
    compose.onNodeWithText("kotlin").assertIsDisplayed()
    compose.onNodeWithText("Submitted by alice").assertIsDisplayed()
    compose.onNodeWithText("https://example.com").performClick()
    compose.runOnIdle {
      assertThat(openedPost)
        .isEqualTo(OpenedPost("id", "https://example.com", "https://lobste.rs/s/id"))
    }
    compose.onNodeWithText("Loaded description").assertDoesNotExist()
    compose.runOnIdle { state.value = NetworkState.Success(details) }
    compose.onNodeWithText("List title").assertIsDisplayed()
    compose.onNodeWithText("API title").assertDoesNotExist()
    compose.onNodeWithText("Loaded description").assertIsDisplayed()
    compose.runOnIdle { displayedHeader.value = null }
    compose.onNodeWithText("API title").assertIsDisplayed()
    compose.onNodeWithText("Submitted by bob").assertIsDisplayed()
    compose.runOnIdle {
      displayedHeader.value = header
      state.value = NetworkState.Error(IllegalStateException(), "Failed to load comments")
    }
    compose.onNodeWithText("List title").assertIsDisplayed()
    compose.onNodeWithText("Failed to load comments").assertIsDisplayed()
  }
}
