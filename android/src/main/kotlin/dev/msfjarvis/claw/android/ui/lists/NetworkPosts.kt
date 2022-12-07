/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.model.LobstersPost

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NetworkPosts(
  lazyPagingItems: LazyPagingItems<Pair<LobstersPost, Boolean>>,
  listState: LazyListState,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val refreshLoadState = lazyPagingItems.loadState.refresh
  val isRefreshing = refreshLoadState == LoadState.Loading
  val pullRefreshState = rememberPullRefreshState(isRefreshing, lazyPagingItems::refresh)
  Box(modifier = modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
    if (lazyPagingItems.itemCount == 0 && refreshLoadState is LoadState.Error) {
      NetworkError(
        label = "Failed to load posts",
        error = refreshLoadState.error,
        modifier = Modifier.align(Alignment.Center),
      )
    } else {
      LazyColumn(
        state = listState,
      ) {
        items(lazyPagingItems) { item ->
          if (item != null) {
            val (post, saved) = item
            LobstersCard(
              post = post.toDbModel(),
              isSaved = saved,
              postActions = postActions,
            )

            Divider()
          }
        }
        if (lazyPagingItems.loadState.append == LoadState.Loading) {
          item {
            ProgressBar(
              modifier =
                Modifier.fillMaxWidth()
                  .wrapContentWidth(Alignment.CenterHorizontally)
                  .padding(top = 4.dp),
            )
          }
        }
      }
    }
    PullRefreshIndicator(
      refreshing = isRefreshing,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      backgroundColor = MaterialTheme.colorScheme.surface,
      contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
    )
  }
}
