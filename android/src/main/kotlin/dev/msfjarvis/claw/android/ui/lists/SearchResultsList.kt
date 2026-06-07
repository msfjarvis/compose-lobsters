/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SearchResultsList(
  lazyPagingItems: LazyPagingItems<UIPost>,
  listState: LazyListState,
  postActions: PostActions,
  filteredTags: ImmutableSet<String>,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  val refreshLoadState by rememberUpdatedState(lazyPagingItems.loadState.refresh)
  val hasNoResults = lazyPagingItems.itemCount == 0 && refreshLoadState is LoadState.NotLoading

  Box(modifier = modifier.fillMaxSize()) {
    if (hasNoResults) {
      Text(
        text = stringResource(R.string.no_search_results),
        modifier = Modifier.align(Alignment.Center),
      )
    } else {
      NetworkPosts(
        lazyPagingItems = lazyPagingItems,
        listState = listState,
        postActions = postActions,
        contentPadding = contentPadding,
        filteredTags = filteredTags,
        modifier = Modifier.fillMaxSize(),
      )
    }
  }
}
