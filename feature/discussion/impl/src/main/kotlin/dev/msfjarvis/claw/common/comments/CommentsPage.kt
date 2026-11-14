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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.msfjarvis.claw.android.ui.navigation.CommentsHeaderData
import dev.msfjarvis.claw.common.posts.PostActions
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
internal fun CommentsPage(
  postId: String,
  commentsUrl: String,
  header: CommentsHeaderData?,
  postActions: PostActions,
  contentPadding: PaddingValues,
  openUserProfile: (String) -> Unit,
  openReplyScreen: (postId: String, commentId: String, commentText: String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: CommentsViewModel = metroViewModel(key = header?.postId ?: postId),
) {
  val resolvedPostId = header?.postId ?: postId
  val resolvedCommentsUrl = header?.commentsUrl ?: commentsUrl
  LaunchedEffect(resolvedPostId, resolvedCommentsUrl) {
    viewModel.loadPostDetails(resolvedPostId, resolvedCommentsUrl)
  }
  val seenCommentsState by
    produceState<SeenCommentsState>(
      initialValue = SeenCommentsState.Loading,
      key1 = resolvedPostId,
    ) {
      value =
        SeenCommentsState.from(
          seenCommentIds = viewModel.getSeenComments(resolvedPostId),
          hasLoaded = true,
        )
    }
  val commentListState = rememberLazyListState()
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

  CommentsPageInternal(
    header = header,
    networkState = viewModel.postDetails,
    postActions = postActions,
    seenCommentsState = seenCommentsState,
    markSeenComments = viewModel::markSeenComments,
    openUserProfile = openUserProfile,
    contentPadding = contentPadding,
    commentListState = commentListState,
    commentNodes = viewModel.commentNodes,
    createCommentNodes = viewModel::createCommentNodes,
    updateCommentNodeExpanded = viewModel::updateCommentNodeExpanded,
    updateUnreadStatus = viewModel::updateUnreadStatus,
    isLoggedIn = isLoggedIn,
    upvoteComment = viewModel::upvoteComment,
    unvoteComment = viewModel::unvoteComment,
    openReplyScreen = { commentId, commentText ->
      openReplyScreen(resolvedPostId, commentId, commentText)
    },
    modifier = modifier.fillMaxSize(),
  )
}
