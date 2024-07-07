/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

@Suppress("LongParameterList")
@Composable
private fun CommentsPageInternal(
  details: UIPost,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  commentState: PostComments?,
  markSeenComments: (String, List<Comment>) -> Unit,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val commentNodes = createListNode(details.comments, commentState).toMutableStateList()
  LaunchedEffect(key1 = commentNodes) {
    if (details.comments.isNotEmpty() && !commentState?.commentIds.isNullOrEmpty()) {
      val unreadCount = details.comments.size - (commentState?.commentIds?.size ?: 0)
      if (unreadCount > 0) {
        val text = "$unreadCount unread comments"
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
      }
    }
    markSeenComments(details.shortId, details.comments)
  }

  Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 24.dp)) {
      item {
        CommentsHeader(
          post = details,
          postActions = postActions,
          htmlConverter = htmlConverter,
          openUserProfile = openUserProfile,
        )
      }

      if (commentNodes.isNotEmpty()) {
        item {
          Text(
            text = "Comments",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }

        nodes(
          nodes = commentNodes,
          htmlConverter = htmlConverter,
          toggleExpanded = { node ->
            val newNode = node.setExpanded(!node.isExpanded)
            val parent = findTopMostParent(newNode)
            val index =
              commentNodes.indexOf(commentNodes.find { it.comment.url == parent.comment.url })
            if (index != -1) {
              commentNodes.removeAt(index)
              commentNodes.add(index, parent)
            }
          },
          openUserProfile = openUserProfile,
        )
      } else {
        item {
          Text(
            text = "No Comments",
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

@Suppress("UNCHECKED_CAST", "LongParameterList")
@Composable
fun CommentsPage(
  postId: String,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  getSeenComments: suspend (String) -> PostComments?,
  markSeenComments: (String, List<Comment>) -> Unit,
  modifier: Modifier = Modifier,
  openUserProfile: (String) -> Unit,
) {
  val postDetails by
    produceState<NetworkState>(Loading) {
      runSuspendCatching { postActions.getComments(postId) }
        .fold(
          success = { details -> value = Success(details) },
          failure = { value = Error(error = it, description = "Failed to load comments") },
        )
    }
  val commentState by
    produceState<PostComments?>(initialValue = null) { value = getSeenComments(postId) }

  when (postDetails) {
    is Success<*> -> {
      CommentsPageInternal(
        details = (postDetails as Success<UIPost>).data,
        postActions = postActions,
        htmlConverter = htmlConverter,
        commentState = commentState,
        markSeenComments = markSeenComments,
        openUserProfile = openUserProfile,
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
