package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.msfjarvis.claw.android.ui.LoadError
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost

@Composable
fun NetworkPosts(
  items: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  isPostSaved: suspend (SavedPost) -> Boolean,
  reloadPosts: () -> Unit,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val loadState = items.loadState.refresh
  val isRefreshing = loadState == LoadState.Loading
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
    if (items.itemCount == 0) {
      Box(modifier = Modifier.fillMaxSize()) {
        if (loadState is LoadState.Error) {
          LoadError(
            label = "Failed to load posts",
            error = loadState.error,
            modifier = Modifier.align(Alignment.Center),
          )
        }
      }
    } else {
      LazyColumn(
        state = listState,
        modifier = modifier,
      ) {
        items(items) { item ->
          if (item != null) {
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
}
