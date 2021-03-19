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
import com.puculek.pulltorefresh.PullToRefresh
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.util.toDbModel

@Composable
fun HottestPosts(
  posts: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  modifier: Modifier = Modifier,
  isPostSaved: (String) -> Boolean,
  saveAction: (SavedPost) -> Unit,
  refreshAction: () -> Unit,
) {
  val urlLauncher = LocalUrlLauncher.current
  var isRefreshing by mutableStateOf(false)

  PullToRefresh(
    isRefreshing = isRefreshing,
    onRefresh = {
      if (posts.loadState.refresh != LoadState.Loading) {
        isRefreshing = isRefreshing.not()
        refreshAction()
      }
    },
  ) {
    if (posts.loadState.refresh == LoadState.Loading) {
      LazyColumn {
        items(15) {
          LoadingLobstersItem()
        }
      }
    } else {
      LazyColumn(
        state = listState,
        modifier = Modifier.then(modifier),
      ) {
        items(posts) { item ->
          if (item != null) {
            @Suppress("NAME_SHADOWING")
            val item = item.toDbModel()
            var isSaved by remember(item.shortId) { mutableStateOf(isPostSaved(item.shortId)) }

            LobstersItem(
              post = item,
              isSaved = isSaved,
              viewPost = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
              viewComments = { urlLauncher.launch(item.commentsUrl) },
              toggleSave = {
                isSaved = isSaved.not()
                saveAction.invoke(item)
              },
            )
          }
        }
      }
    }
  }
}
