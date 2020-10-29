package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.data.LobstersViewModel
import dev.msfjarvis.lobsters.urllauncher.UrlLauncherAmbient

@Composable
fun SavedPosts(
  viewModel: LobstersViewModel,
) {
  val posts by viewModel.savedPosts.collectAsState()
  val listState = rememberLazyListState()
  val urlLauncher = UrlLauncherAmbient.current

  if (posts.isEmpty()) {
    EmptyList(saved = true)
  } else {
    LazyColumnFor(
      items = posts,
      state = listState,
      modifier = Modifier.padding(horizontal = 8.dp)
    ) { item ->
      LobstersItem(
        post = item,
        linkOpenAction = { post -> urlLauncher.launch(post.url.ifEmpty { post.commentsUrl }) },
        commentOpenAction = { post -> urlLauncher.launch(post.commentsUrl) },
        saveAction = viewModel::removeSavedPost
      )
    }
  }
}


