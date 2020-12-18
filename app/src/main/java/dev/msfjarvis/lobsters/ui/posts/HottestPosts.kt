package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncherAmbient

@Composable
fun HottestPosts(
  posts: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
) {
  val urlLauncher = UrlLauncherAmbient.current

  if (posts.loadState.refresh == LoadState.Loading) {
    EmptyList(saved = false)
  } else {
    LazyColumn(
      state = listState,
      modifier = Modifier.then(modifier),
    ) {
      items(posts) { item ->
        if (item != null) {
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
}
