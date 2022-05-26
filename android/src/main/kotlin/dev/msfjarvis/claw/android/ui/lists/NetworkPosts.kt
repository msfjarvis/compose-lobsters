package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.common.ui.Divider
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost

@Composable
fun NetworkPosts(
  items: List<LobstersPost>,
  isRefreshing: Boolean,
  listState: LazyListState,
  isPostSaved: suspend (SavedPost) -> Boolean,
  reloadPosts: () -> Unit,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  SwipeRefresh(
    state = rememberSwipeRefreshState(isRefreshing),
    onRefresh = reloadPosts,
    indicator = { state, trigger ->
      val backgroundColor = MaterialTheme.colorScheme.surface
      SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = trigger,
        backgroundColor = backgroundColor,
        contentColor = contentColorFor(backgroundColor)
      )
    }
  ) {
    if (items.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize())
    } else {
      LazyColumn(
        state = listState,
        modifier = modifier,
      ) {
        items(items) { item ->
          val dbModel = item.toDbModel()
          ListItem(
            item = dbModel,
            isSaved = isPostSaved,
            postActions = postActions,
          )
          Divider()
        }
      }
    }
  }
}
