package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.lobsters.data.local.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher

@Composable
fun HottestPosts(
  posts: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  isPostSaved: (String) -> Boolean,
  modifier: Modifier = Modifier,
  saveAction: (LobstersPost) -> Unit,
) {
  val urlLauncher = LocalUrlLauncher.current

  if (posts.loadState.refresh == LoadState.Loading) {
    EmptyList(saved = false)
  } else {
    LazyColumn(
      state = listState,
      modifier = Modifier.then(modifier),
    ) {
      items(posts) { item ->
        if (item != null) {
          var isSaved by remember(item.short_id) { mutableStateOf(isPostSaved(item.short_id)) }

          LobstersItem(
            post = item,
            isSaved = isSaved,
            onClick = { urlLauncher.launch(item.url.ifEmpty { item.comments_url }) },
            onLongClick = { urlLauncher.launch(item.comments_url) },
            onSaveButtonClick = {
              isSaved = isSaved.not()
              saveAction.invoke(item)
            },
          )
        }
      }
    }
  }
}
