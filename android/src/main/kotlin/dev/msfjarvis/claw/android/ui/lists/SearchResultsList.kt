/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SearchResultsList(
  lazyPagingItems: LazyPagingItems<UIPost>,
  listState: LazyListState,
  postActions: PostActions,
  filteredTags: ImmutableSet<String>,
  modifier: Modifier = Modifier,
) {
  NetworkPosts(
    lazyPagingItems = lazyPagingItems,
    listState = listState,
    postActions = postActions,
    contentPadding = PaddingValues(0.dp),
    filteredTags = filteredTags,
    modifier = modifier,
  )
}
