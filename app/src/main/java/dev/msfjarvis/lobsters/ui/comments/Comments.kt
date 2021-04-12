package dev.msfjarvis.lobsters.ui.comments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import dev.msfjarvis.lobsters.model.LobstersPostDetails
import dev.msfjarvis.lobsters.ui.posts.PostTitle
import dev.msfjarvis.lobsters.ui.posts.SubmitterName
import dev.msfjarvis.lobsters.ui.posts.TagRow
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.get

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
  modifier: Modifier = Modifier,
) {
  LazyColumn(Modifier.then(modifier)) {
    item { CommentsHeader(postDetails = details) }

    item { Spacer(modifier = Modifier.height(8.dp)) }

    items(details.comments) { CommentEntry(it) }

    item { Divider(color = Color.Gray.copy(0.4f)) }
  }
}

@Suppress("UNCHECKED_CAST")
@Composable
fun CommentsPage(
  postId: String,
  paddingValues: PaddingValues,
  scaffoldState: ScaffoldState,
  getDetails: suspend (String) -> LobstersPostDetails,
) {
  var postDetails: NetworkState by remember { mutableStateOf(NetworkState.Loading) }

  LaunchedEffect(postId) {
    postDetails = try {
      NetworkState.Success(getDetails(postId))
    } catch (e: Throwable) {
      NetworkState.Error(e.message ?: "Failed to load posts")
    }
  }

  when (postDetails) {
    is NetworkState.Success<*> -> {
      CommentsPageInternal(
        details = (postDetails as NetworkState.Success<LobstersPostDetails>).data,
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
      )
    }
    is NetworkState.Error -> {
      LaunchedEffect(scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(
          message = (postDetails as NetworkState.Error).message,
        )
      }
    }
    NetworkState.Loading -> ProgressBar(paddingValues.calculateBottomPadding())
  }
}

@Composable
private fun CommentsHeader(postDetails: LobstersPostDetails) {
  Surface {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
      PostTitle(
        title = postDetails.title,
        modifier = Modifier.padding(bottom = 4.dp),
      )
      Row {
        TagRow(
          tags = postDetails.tags,
        )
        Spacer(
          modifier = Modifier.weight(1f),
        )
      }

      if (postDetails.description.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = postDetails.description)
      } else {
        Spacer(modifier = Modifier.height(12.dp))
      }

      SubmitterName(
        text = postDetails.submitter.username,
        avatarUrl = postDetails.submitter.avatarUrl,
        contentDescription = Strings.AvatarContentDescription.get(postDetails.submitter.username),
      )
    }
  }
}

@Composable
private fun ProgressBar(bottomPadding: Dp) {
  Box(
    modifier = Modifier.padding(bottom = bottomPadding).fillMaxSize(),
    contentAlignment = Alignment.Center
  ) { CircularProgressIndicator(color = MaterialTheme.colors.secondary) }
}
