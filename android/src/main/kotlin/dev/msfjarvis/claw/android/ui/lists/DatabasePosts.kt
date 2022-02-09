package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost

@OptIn(ExperimentalMaterialApi::class)
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
      val dismissState =
        rememberDismissState(
          confirmStateChange = {
            postActions.toggleSave(item)
            true
          }
        )
      SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {},
      ) {
        ListItem(
          item = item,
          isSaved = isSaved,
          postActions = postActions,
        )
      }
    }
  }
}
