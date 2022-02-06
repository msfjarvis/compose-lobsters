package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
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
      var saved by remember(item) { mutableStateOf(false) }
      LaunchedEffect(item) { saved = isSaved(item) }
      LobstersCard(
        post = item,
        isSaved = saved,
        postActions = postActions,
        modifier = modifier,
      )
    }
  }
}
