/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.posts.TEST_POST_ACTIONS
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.model.UIPost
import kotlinx.coroutines.flow.MutableStateFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkPosts(
  lazyPagingItems: LazyPagingItems<UIPost>,
  listState: LazyListState,
  postActions: PostActions,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  onPostClick: (String) -> Unit
) {
  ReportDrawnWhen { lazyPagingItems.itemCount > 0 }
  val refreshLoadState by rememberUpdatedState(lazyPagingItems.loadState.refresh)
  val state = rememberPullToRefreshState()
  PullToRefreshBox(
    modifier = modifier.fillMaxSize(),
    isRefreshing = refreshLoadState == LoadState.Loading,
    state = state,
    onRefresh = { lazyPagingItems.refresh() },
    indicator = {
      PullToRefreshDefaults.Indicator(
        state = state,
        isRefreshing = refreshLoadState == LoadState.Loading,
        modifier = Modifier.align(Alignment.TopCenter).padding(contentPadding),
      )
    },
  ) {
    if (lazyPagingItems.itemCount == 0 && refreshLoadState is LoadState.Error) {
      NetworkError(
        label = "Failed to load posts",
        error = (refreshLoadState as LoadState.Error).error,
        modifier = Modifier.align(Alignment.Center),
      )
    } else {
      LazyColumn(contentPadding = contentPadding, state = listState) {
        items(
          count = lazyPagingItems.itemCount,
          key = lazyPagingItems.itemKey { it.shortId },
          contentType = lazyPagingItems.itemContentType { "LobstersItem" },
        ) { index ->
          val item = lazyPagingItems[index]
          if (item != null) {
            LobstersListItem(item = item, postActions = postActions,
              modifier = Modifier.clickable {
              onPostClick(item.shortId) // Trigger the click listener
            })
            HorizontalDivider()
          }
        }
        if (lazyPagingItems.loadState.append == LoadState.Loading) {
          item(key = "progressbar") {
            ProgressBar(
              modifier =
                Modifier.fillMaxWidth()
                  .wrapContentWidth(Alignment.CenterHorizontally)
                  .padding(vertical = 16.dp)
            )
          }
        }
      }
    }
  }
}

@DevicePreviews
@Composable
private fun ListPreview() {
  val items = List(20) { TEST_POST.copy(shortId = "${TEST_POST.shortId}${it}") }
  val flow = MutableStateFlow(PagingData.from(items))
  LobstersTheme {
    NetworkPosts(
      lazyPagingItems = flow.collectAsLazyPagingItems(),
      listState = rememberLazyListState(),
      postActions = TEST_POST_ACTIONS,
      contentPadding = PaddingValues(),
      onPostClick = {}
    )
  }
}
