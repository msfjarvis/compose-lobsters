package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.AmbientUrlLauncher

@Composable
fun SavedPosts(
  posts: List<LobstersPost>,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
) {
  val listState = rememberLazyListState()
  val urlLauncher = AmbientUrlLauncher.current

  if (posts.isEmpty()) {
    EmptyList(saved = true)
  } else {
    LazyColumn(
      state = listState,
      modifier = Modifier.then(modifier)
    ) {
      items(posts) { item ->
        LobstersItem(
          post = item,
          onClick = { urlLauncher.launch(item.url.ifEmpty { item.comments_url }) },
          onLongClick = { urlLauncher.launch(item.comments_url) },
          onSaveButtonClick = { saveAction.invoke(item) },
        )
      }
    }
  }
}
