package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
  modifier: Modifier = Modifier,
) {
  LazyColumn(modifier) {
    item { CommentsHeader(postDetails = details) }

    item { Spacer(modifier = Modifier.height(8.dp)) }

    items(details.comments) { item -> CommentEntry(item) }

    item { Divider(color = Color.Gray.copy(0.4f)) }
  }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun CommentsPage(
  postId: String,
  getDetails: suspend (String) -> LobstersPostDetails,
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
        modifier,
      )
    }
    is NetworkState.Error -> TODO("Handle no network scenario")
    NetworkState.Loading -> ProgressBar(modifier)
  }
}

@Composable
private fun ProgressBar(
  modifier: Modifier,
) {
  Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
  }
}
