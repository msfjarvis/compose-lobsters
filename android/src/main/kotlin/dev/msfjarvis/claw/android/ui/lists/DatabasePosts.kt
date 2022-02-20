package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.Divider
import dev.msfjarvis.claw.database.local.SavedPost

@Composable
fun DatabasePosts(
  items: List<SavedPost>,
  listState: LazyListState,
  isSaved: suspend (SavedPost) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    state = listState,
    modifier = modifier,
  ) {
    items(items) { item ->
      ListItem(
        item = item,
        isSaved = isSaved,
        postActions = postActions,
      )

      Divider()
    }
  }
}
