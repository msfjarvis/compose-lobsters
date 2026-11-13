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
import dev.msfjarvis.claw.core.NetworkState.Success
import dev.msfjarvis.claw.model.UIPost
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
  viewModel: CommentsViewModel = metroViewModel(key = postId),
) {
  LaunchedEffect(postId, commentsUrl) {
    viewModel.loadPostDetails(postId, commentsUrl)
  }
  val seenCommentsState by
    produceState<SeenCommentsState>(initialValue = SeenCommentsState.Loading, key1 = postId) {
      value =
        SeenCommentsState.from(seenCommentIds = viewModel.getSeenComments(postId), hasLoaded = true)
    }
  val commentListState = rememberLazyListState()
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

  val postDetails = viewModel.postDetails
  val details = (postDetails as? Success<*>)?.data as? UIPost
  val displayedHeader =
    header
      ?: details?.let {
        CommentsHeaderData(it.title, it.tags, it.url, it.submitter, it.userIsAuthor)
      }

  CommentsPageInternal(
    header = displayedHeader,
    postId = postId,
    commentsUrl = commentsUrl,
    details = details,
    networkState = postDetails,
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
      openReplyScreen(postId, commentId, commentText)
    },
    modifier = modifier.fillMaxSize(),
  )
}
