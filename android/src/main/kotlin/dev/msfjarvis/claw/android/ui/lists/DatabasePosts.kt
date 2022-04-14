package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.android.ui.asZonedDateTime
import dev.msfjarvis.claw.android.ui.decorations.MonthHeader
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.Divider
import dev.msfjarvis.claw.database.local.SavedPost

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatabasePosts(
  items: List<SavedPost>,
  listState: LazyListState,
  isSaved: suspend (SavedPost) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val sorted = items.sortedBy { post -> post.createdAt.asZonedDateTime() }
  val grouped = sorted.groupBy { post -> post.createdAt.asZonedDateTime().month }
  LazyColumn(
    state = listState,
    modifier = modifier,
  ) {
    grouped.forEach { (month, posts) ->
      stickyHeader { MonthHeader(month = month) }
      items(posts) { item ->
        ListItem(
          item = item,
          isSaved = isSaved,
          postActions = postActions,
        )

        Divider()
      }
    }
  }
}
