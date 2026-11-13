/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.msfjarvis.claw.android.ui.navigation.CommentsHeaderData
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.core.NetworkState
import dev.msfjarvis.claw.feature.discussion.impl.R
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.UIPost
import kotlinx.coroutines.flow.StateFlow

internal data class CommentsVisitDecision(val unreadCount: Int, val shouldPersist: Boolean)

internal fun buildCommentsVisitDecision(
  currentCommentIds: List<String>,
  seenCommentsState: SeenCommentsState,
): CommentsVisitDecision {
  return when (seenCommentsState) {
    SeenCommentsState.Loading -> CommentsVisitDecision(unreadCount = 0, shouldPersist = false)
    SeenCommentsState.NoBaseline -> CommentsVisitDecision(unreadCount = 0, shouldPersist = true)
    is SeenCommentsState.BaselineLoaded -> {
      val seenCommentIds = seenCommentsState.seenCommentIds
      CommentsVisitDecision(
        unreadCount = currentCommentIds.count { it !in seenCommentIds },
        shouldPersist = true,
      )
    }
  }
}

@Composable
internal fun CommentsPageInternal(
  header: CommentsHeaderData?,
  networkState: NetworkState,
  postActions: PostActions,
  seenCommentsState: SeenCommentsState,
  markSeenComments: (String, List<Comment>) -> Unit,
  openUserProfile: (String) -> Unit,
  contentPadding: PaddingValues,
  commentListState: LazyListState,
  commentNodes: StateFlow<List<CommentNode>>,
  createCommentNodes: (UIPost, SeenCommentsState) -> Unit,
  updateCommentNodeExpanded: (String, Boolean) -> Unit,
  updateUnreadStatus: (SeenCommentsState) -> Unit,
  isLoggedIn: Boolean,
  upvoteComment: (String) -> Unit,
  unvoteComment: (String) -> Unit,
  openReplyScreen: (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val details = (networkState as? NetworkState.Success<*>)?.data as? UIPost
  val displayedHeader =
    header
      ?: details?.let {
        CommentsHeaderData(
          it.shortId,
          it.commentsUrl,
          it.title,
          it.tags,
          it.url,
          it.submitter,
          it.userIsAuthor,
        )
      }

  LaunchedEffect(key1 = details, key2 = seenCommentsState) {
    if (details != null && seenCommentsState != SeenCommentsState.Loading) {
      createCommentNodes(details, seenCommentsState)
    }
  }

  LaunchedEffect(key1 = seenCommentsState) { updateUnreadStatus(seenCommentsState) }

  val onToggleExpandedState = { shortId: String, isExpanded: Boolean ->
    updateCommentNodeExpanded(shortId, isExpanded)
  }

  val context = LocalContext.current
  val commentNodes by commentNodes.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = details, key2 = seenCommentsState) {
    if (details == null || seenCommentsState == SeenCommentsState.Loading) return@LaunchedEffect
    val decision =
      buildCommentsVisitDecision(
        currentCommentIds = details.comments.map(Comment::shortId),
        seenCommentsState = seenCommentsState,
      )
    if (decision.unreadCount > 0) {
      val text =
        when (decision.unreadCount) {
          1 -> "${decision.unreadCount} unread comment"
          else -> "${decision.unreadCount} unread comments"
        }
      Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
    if (decision.shouldPersist) {
      markSeenComments(details.shortId, details.comments)
    }
  }

  Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding, state = commentListState) {
      if (displayedHeader != null) {
        item(key = "post_header") {
          CommentsHeader(
            header = displayedHeader,
            description = details?.description.orEmpty(),
            postActions = postActions,
            openUserProfile = openUserProfile,
            modifier = Modifier.testTag("comments_header"),
          )
        }
      }

      if (networkState is NetworkState.Error) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center,
          ) {
            NetworkError(label = networkState.description, error = networkState.error)
          }
        }
      } else if (details == null || seenCommentsState == SeenCommentsState.Loading) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center,
          ) {
            ProgressBar()
          }
        }
      } else if (commentNodes.isNotEmpty()) {
        item(key = "comments_header") {
          Text(
            text = stringResource(R.string.comments_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }

        items(items = commentNodes, key = { node -> node.comment.shortId }) { node ->
          NodeBox(
            node = node,
            isExpanded = node.isExpanded,
            openUserProfile = openUserProfile,
            onToggleExpandedState = onToggleExpandedState,
            isLoggedIn = isLoggedIn,
            upvoteComment = upvoteComment,
            unvoteComment = unvoteComment,
            openReplyScreen = openReplyScreen,
            shareComment = postActions::shareComment,
          )
        }

        item(key = "bottom_spacer") {
          Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
      } else {
        item {
          Text(
            text = stringResource(R.string.no_comments),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

@Composable
private fun NodeBox(
  node: CommentNode,
  isExpanded: Boolean,
  openUserProfile: (String) -> Unit,
  onToggleExpandedState: (String, Boolean) -> Unit,
  isLoggedIn: Boolean,
  upvoteComment: (String) -> Unit,
  unvoteComment: (String) -> Unit,
  openReplyScreen: (String, String) -> Unit,
  shareComment: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  CommentEntry(
    isExpanded = isExpanded,
    commentNode = node,
    openUserProfile = openUserProfile,
    onToggleExpandedState = onToggleExpandedState,
    isLoggedIn = isLoggedIn,
    upvoteComment = upvoteComment,
    unvoteComment = unvoteComment,
    onReply = openReplyScreen,
    onShare = shareComment,
    modifier = modifier,
  )
  HorizontalDivider()
}
