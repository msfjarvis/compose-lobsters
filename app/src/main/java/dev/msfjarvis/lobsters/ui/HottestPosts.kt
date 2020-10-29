package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.data.LobstersViewModel
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher

@Composable
fun HottestPosts(
  lastIndex: Int,
  urlLauncher: UrlLauncher,
  viewModel: LobstersViewModel,
) {
  val posts by viewModel.posts.collectAsState()
  val listState = rememberLazyListState()

  if (posts.isEmpty()) {
    EmptyList(saved = false)
  } else {
    LazyColumnForIndexed(
      items = posts,
      state = listState,
      modifier = Modifier.padding(horizontal = 8.dp)
    ) { index, item ->
      if (lastIndex == index) {
        viewModel.getMorePosts()
      }
      LobstersItem(
        post = item,
        linkOpenAction = { post -> urlLauncher.launch(post.url.ifEmpty { post.commentsUrl }) },
        commentOpenAction = { post -> urlLauncher.launch(post.commentsUrl) },
        saveAction = viewModel::savePost
      )
    }
  }
}
