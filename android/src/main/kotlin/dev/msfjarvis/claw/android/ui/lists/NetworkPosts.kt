/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
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
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkPosts(
  lazyPagingItems: LazyPagingItems<UIPost>,
  listState: LazyListState,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  ReportDrawnWhen { lazyPagingItems.itemCount > 0 }
  val refreshLoadState = lazyPagingItems.loadState.refresh
  val isRefreshing = refreshLoadState == LoadState.Loading && lazyPagingItems.itemCount == 0
  val pullRefreshState = rememberPullRefreshState(isRefreshing, lazyPagingItems::refresh)
  Box(modifier = modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
    if (lazyPagingItems.itemCount == 0 && refreshLoadState is LoadState.Error) {
      NetworkError(
        label = "Failed to load posts",
        error = refreshLoadState.error,
        modifier = Modifier.align(Alignment.Center),
      )
    } else {
      LazyColumn(state = listState) {
        items(
          count = lazyPagingItems.itemCount,
          key = lazyPagingItems.itemKey { item -> "${item.shortId}-${item.hashCode()}" },
          contentType = lazyPagingItems.itemContentType { "LobstersItem" },
        ) { index ->
          val item = lazyPagingItems[index]
          if (item != null) {
            LobstersListItem(
              item = item,
              postActions = postActions,
              refresh = { lazyPagingItems.refresh() },
            )
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
    PullRefreshIndicator(
      refreshing = isRefreshing,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      shadowElevation = 6.dp, // From M2 implementation
    )
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
    )
  }
}
