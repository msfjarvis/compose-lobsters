package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.util.asZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedPosts(
  posts: List<SavedPost>,
  modifier: Modifier = Modifier,
  saveAction: (SavedPost) -> Unit,
) {
  val listState = rememberLazyListState()
  val urlLauncher = LocalUrlLauncher.current

  if (posts.isEmpty()) {
    EmptyList(saved = true)
  } else {
    LazyColumn(
      state = listState,
      modifier = Modifier.then(modifier),
    ) {
      val grouped = posts.groupBy { it.createdAt.asZonedDateTime().month }
      grouped.forEach { (month, posts) ->
        stickyHeader {
          MonthHeader(month = month)
        }
        items(posts) { item ->
          LobstersItem(
            post = item,
            isSaved = true,
            onClick = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
            onLongClick = { urlLauncher.launch(item.commentsUrl) },
            onSaveButtonClick = { saveAction.invoke(item) },
          )
        }
      }
    }
  }
}
