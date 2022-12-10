/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  modifier: Modifier = Modifier,
) {
  val commentNodes = createListNode(details.comments).toMutableStateList()

  Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 24.dp)) {
      item {
        CommentsHeader(
          postDetails = details,
          postActions = postActions,
          htmlConverter = htmlConverter,
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

        item {
          DisplayListNode(comments = commentNodes, htmlConverter = htmlConverter) { node ->
            val newNode = toggleAllExpanded(node)
            val index =
              commentNodes.indexOf(commentNodes.find { it.comment.url == node.comment.url })
            if (index == -1) {
              error("Failed to find node for comment: ${node.comment}")
            } else {
              commentNodes.removeAt(index)
              commentNodes.add(index, newNode)
            }
          }
        }
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

@Suppress("UNCHECKED_CAST")
@Composable
fun CommentsPage(
  postId: String,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  modifier: Modifier = Modifier,
) {
  val postDetails by
    produceState<NetworkState>(Loading) {
      runCatching { postActions.getComments(postId) }
        .fold(
          onSuccess = { details -> value = Success(details) },
          onFailure = { value = Error(error = it, description = "Failed to load comments") }
        )
    }

  when (postDetails) {
    is Success<*> -> {
      CommentsPageInternal(
        details = (postDetails as Success<LobstersPostDetails>).data,
        postActions = postActions,
        htmlConverter = htmlConverter,
        modifier = modifier.fillMaxSize(),
      )
    }
    is Error -> {
      val error = postDetails as Error
      NetworkError(label = error.description, error = error.error)
    }
    Loading -> ProgressBar()
  }
}
