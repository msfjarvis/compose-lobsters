/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.UIPost
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Suppress("UNCHECKED_CAST")
@Composable
fun CommentsPage(
  postId: String,
  postActions: PostActions,
  contentPadding: PaddingValues,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: CommentsViewModel = metroViewModel(key = postId),
) {
  LaunchedEffect(postId) { viewModel.loadPostDetails(postId) }
  val seenCommentsState by
    produceState<SeenCommentsState>(initialValue = SeenCommentsState.Loading, key1 = postId) {
      value =
        SeenCommentsState.from(postComments = viewModel.getSeenComments(postId), hasLoaded = true)
    }
  val commentListState = rememberLazyListState()

  when (val postDetails = viewModel.postDetails) {
    is Success<*> -> {
      if (seenCommentsState != SeenCommentsState.Loading) {
        CommentsPageInternal(
          details = (postDetails as Success<UIPost>).data,
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
          modifier = modifier.fillMaxSize(),
        )
      } else {
        Box(modifier = Modifier.fillMaxSize()) {
          ProgressBar(modifier = Modifier.align(Alignment.Center))
        }
      }
    }
    is Error -> {
      Box(modifier = Modifier.fillMaxSize()) {
        NetworkError(
          label = postDetails.description,
          error = postDetails.error,
          modifier = Modifier.align(Alignment.Center),
        )
      }
    }
    Loading -> {
      Box(modifier = Modifier.fillMaxSize()) {
        ProgressBar(modifier = Modifier.align(Alignment.Center))
      }
    }
  }
}
