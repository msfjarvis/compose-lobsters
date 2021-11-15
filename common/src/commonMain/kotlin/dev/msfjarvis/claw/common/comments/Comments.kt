package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.lobsters.ui.comments.NetworkState

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
  bottomPadding: Dp,
) {
  LazyColumn(Modifier.padding(bottom = bottomPadding)) {
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
  paddingValues: PaddingValues,
) {
  var postDetails: NetworkState by remember { mutableStateOf(NetworkState.Loading) }

  LaunchedEffect(postId) { postDetails = NetworkState.Success(getDetails(postId)) }

  when (postDetails) {
    is NetworkState.Success<*> -> {
      CommentsPageInternal(
        (postDetails as NetworkState.Success<LobstersPostDetails>).data,
        paddingValues.calculateBottomPadding(),
      )
    }
    is NetworkState.Error -> TODO("Handle no network scenario")
    NetworkState.Loading -> ProgressBar(paddingValues.calculateBottomPadding())
  }
}

@Composable
private fun ProgressBar(bottomPadding: Dp) {
  Box(
    modifier = Modifier.padding(bottom = bottomPadding).fillMaxSize(),
    contentAlignment = Alignment.Center
  ) { CircularProgressIndicator(color = MaterialTheme.colors.secondary) }
}
