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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.UIPost

@Suppress("UNCHECKED_CAST")
@Composable
fun CommentsPage(
  postId: String,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  getSeenComments: suspend (String) -> PostComments?,
  markSeenComments: (String, List<Comment>) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  openUserProfile: (String) -> Unit,
) {
  val postDetails by
    produceState<NetworkState>(Loading, key1 = postId) {
      runSuspendCatching { postActions.getComments(postId) }
        .fold(
          success = { details -> value = Success(details) },
          failure = { value = Error(error = it, description = "Failed to load comments") },
        )
    }
  val commentState by
    produceState<PostComments?>(initialValue = null, key1 = postId) {
      value = getSeenComments(postId)
    }

  when (postDetails) {
    is Success<*> -> {
      CommentsPageInternal(
        details = (postDetails as Success<UIPost>).data,
        postActions = postActions,
        htmlConverter = htmlConverter,
        commentState = commentState,
        markSeenComments = markSeenComments,
        openUserProfile = openUserProfile,
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize(),
      )
    }
    is Error -> {
      val error = postDetails as Error
      Box(modifier = Modifier.fillMaxSize()) {
        NetworkError(
          label = error.description,
          error = error.error,
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
