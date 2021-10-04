package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.launch

@Composable
fun HottestPosts(
  items: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  isPostSaved: suspend (SavedPost) -> Boolean,
  toggleSave: suspend (SavedPost) -> Unit,
  reloadPosts: () -> Unit,
  launchUrl: (String) -> Unit,
  viewComments: (String) -> Unit,
  modifier: Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  val isRefreshing = items.loadState.refresh == LoadState.Loading
  SwipeRefresh(
    state = rememberSwipeRefreshState(isRefreshing),
    onRefresh = reloadPosts,
  ) {
    if (items.itemCount == 0) {
      Box(modifier = Modifier.fillMaxSize())
    } else {
      NetworkPosts(
        items = items,
        launchUrl = launchUrl,
        listState = listState,
        isSaved = isPostSaved,
        viewComments = viewComments,
        toggleSave = { coroutineScope.launch { toggleSave(it) } },
        modifier = Modifier.padding(top = 16.dp).then(modifier),
      )
    }
  }
}
