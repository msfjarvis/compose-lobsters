package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncherAmbient

@Composable
fun SavedPosts(
  posts: List<LobstersPost>,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
) {
  val listState = rememberLazyListState()
  val urlLauncher = UrlLauncherAmbient.current

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
          onClick = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
          onLongClick = { urlLauncher.launch(item.commentsUrl) },
          onSaveButtonClick = { saveAction.invoke(item) },
        )
      }
    }
  }
}
