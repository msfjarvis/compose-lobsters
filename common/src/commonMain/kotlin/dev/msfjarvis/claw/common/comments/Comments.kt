package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.Divider
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 24.dp)) {
      item { CommentsHeader(postDetails = details, postActions = postActions) }

      if (details.commentCount > 0) {
        item {
          Text(
            text = "Comments",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }

        itemsIndexed(details.comments) { index, item ->
          if (index != 0) {
            Divider()
          }
          CommentEntry(item)
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
  getDetails: suspend (String) -> LobstersPostDetails,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val postDetails by
    produceState<NetworkState>(NetworkState.Loading) {
      value = NetworkState.Success(getDetails(postId))
    }

  when (postDetails) {
    is NetworkState.Success<*> -> {
      CommentsPageInternal(
        (postDetails as NetworkState.Success<LobstersPostDetails>).data,
        postActions,
        modifier.fillMaxSize(),
      )
    }
    is NetworkState.Error -> TODO("Handle no network scenario")
    NetworkState.Loading -> ProgressBar(modifier)
  }
}
