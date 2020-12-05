package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncherAmbient

@Composable
fun HottestPosts(
  posts: List<LobstersPost>,
  listState: LazyListState,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
) {
  val urlLauncher = UrlLauncherAmbient.current

  if (posts.isEmpty()) {
    EmptyList(saved = false)
  } else {
    LazyColumnFor(
      items = posts,
      state = listState,
      modifier = Modifier.padding(horizontal = 8.dp).then(modifier)
    ) { item ->
      LobstersItem(
        post = item,
        onClick = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
        onLongClick = { urlLauncher.launch(item.commentsUrl) },
        onSaveButtonClick = { saveAction.invoke(item) },
      )
    }
  }
}
