/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.claw.android.ui.lists.SearchList
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun SearchScreen(
  postActions: PostActions,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = metroViewModel(),
) {
  val listState = rememberLazyListState()
  val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
  SearchList(
    lazyPagingItems = searchResults,
    listState = listState,
    postActions = postActions,
    searchQuery = viewModel.searchQuery,
    contentPadding = contentPadding,
    setSearchQuery = { query -> viewModel.searchQuery = query },
    modifier = modifier,
  )
}
