package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost

@Composable
fun NetworkPosts(
  items: LazyPagingItems<LobstersPost>,
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
      if (item != null) {
        val dbModel = item.toDbModel()
        ListItem(
          item = dbModel,
          isSaved = isSaved,
          postActions = postActions,
        )

        Divider()
      }
    }
  }
}
