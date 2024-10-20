/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.compose.LazyPagingItems
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.SearchBar
import dev.msfjarvis.claw.model.UIPost

@Composable
fun SearchList(
  lazyPagingItems: LazyPagingItems<UIPost>,
  listState: LazyListState,
  postActions: PostActions,
  searchQuery: String,
  setSearchQuery: (String) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  onPostClick: (String) -> Unit
) {
  val triggerSearch = { query: String ->
    setSearchQuery(query)
    lazyPagingItems.refresh()
  }
  Column(
    modifier =
      modifier.systemBarsPadding().semantics { isTraversalGroup = true }.zIndex(1f).fillMaxWidth()
  ) {
    SearchBar(
      value = searchQuery,
      onValueChange = setSearchQuery,
      onSearch = { triggerSearch(it) },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("search_bar"),
    )
    NetworkPosts(
      lazyPagingItems = lazyPagingItems,
      listState = listState,
      postActions = postActions,
      contentPadding = contentPadding,
      onPostClick = onPostClick
    )
  }
}
