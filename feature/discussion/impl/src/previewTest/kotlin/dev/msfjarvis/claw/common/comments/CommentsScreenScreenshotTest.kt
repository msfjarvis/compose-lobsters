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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.core.NetworkState
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow

private val sampleComment =
  Comment(
    shortId = "comment",
    comment = "<p>A sample discussion with <strong>formatted text</strong>.</p>",
    score = 42,
    timestamp = Instant.parse("2020-01-01T00:00:00Z"),
    edited = false,
    parentComment = null,
    sourceStoryId = "story",
    user = "alice",
    isUpvoted = false,
  )

private val samplePost =
  UIPost(
    discussion =
      Discussion(
        stories =
          listOf(
            SourceStory(
              "story",
              "2020-01-01T00:00:00Z",
              "A sample story",
              "https://example.com",
              "bob",
              listOf("kotlin"),
            )
          ),
        comments = listOf(sampleComment),
      ),
    description = "A story description.",
    commentCount = 1,
    commentsUrl = "https://lobste.rs/s/story/c",
  )

private val previewActions =
  object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) = Unit

    override fun viewComments(post: UIPost) = Unit

    override fun viewCommentsPage(post: UIPost) = Unit

    override fun toggleSave(post: UIPost) = Unit

    override fun share(post: UIPost) = Unit

    override fun shareComment(commentId: String) = Unit

    override fun isPostRead(post: UIPost) = false

    override fun isPostSaved(post: UIPost) = false

    override suspend fun getLinkMetadata(url: String) = LinkMetadata(url, null)
  }

@OptIn(ExperimentalCoilApi::class)
@PreviewTest
@Preview(name = "compact", widthDp = 360, heightDp = 800)
@Preview(name = "medium", widthDp = 700, heightDp = 900)
@Preview(name = "expanded", widthDp = 1200, heightDp = 800)
@Preview(name = "short-large-font", widthDp = 900, heightDp = 480, fontScale = 1.5f)
@Composable
private fun CommentsScreenScreenshot() {
  val imageHandler = AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(null) }
  val nodes =
    MutableStateFlow(listOf(CommentNode(sampleComment, isPostAuthor = false, indentLevel = 0)))
  CompositionLocalProvider(LocalAsyncImagePreviewHandler provides imageHandler) {
    LobstersTheme {
      CommentsPageInternal(
        header = null,
        networkState = NetworkState.Success(samplePost),
        postActions = previewActions,
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
}
