/*
 * Copyright © 2021-2023 Harsh Shandilya.
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.NetworkError
import dev.msfjarvis.claw.common.ui.ProgressBar
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.toSavedPost

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NetworkPosts(
  lazyPagingItems: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  isPostSaved: suspend (SavedPost) -> Boolean,
  isPostRead: suspend (String) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  ReportDrawnWhen { lazyPagingItems.itemCount > 0 }
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
        items(
          count = lazyPagingItems.itemCount,
          key = lazyPagingItems.itemKey { it.shortId },
          contentType = lazyPagingItems.itemContentType { "LobstersItem" },
        ) { index ->
          val item = lazyPagingItems[index]
          if (item != null) {
            val dbModel = item.toSavedPost()
            ListItem(
              item = dbModel,
              isSaved = isPostSaved,
              isRead = isPostRead,
              postActions = postActions,
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
                  .padding(vertical = 16.dp),
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
