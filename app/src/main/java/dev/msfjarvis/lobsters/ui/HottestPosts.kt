package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.urllauncher.UrlLauncherAmbient

@Composable
fun HottestPosts(
  posts: List<LobstersPost>,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
  overscrollAction: () -> Unit,
) {
  val listState = rememberLazyListState()
  val urlLauncher = UrlLauncherAmbient.current

  if (posts.isEmpty()) {
    EmptyList(saved = false)
  } else {
    LazyColumnForIndexed(
      items = posts,
      state = listState,
      modifier = Modifier.padding(horizontal = 8.dp).then(modifier)
    ) { index, item ->
      if (posts.lastIndex == index) {
        overscrollAction.invoke()
      }
      LobstersItem(
        post = item,
        linkOpenAction = { post -> urlLauncher.launch(post.url.ifEmpty { post.commentsUrl }) },
        commentOpenAction = { post -> urlLauncher.launch(post.commentsUrl) },
        saveAction = saveAction,
      )
    }
  }
}
