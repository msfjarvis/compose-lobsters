/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.toSavedPost
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchList(
  items: Flow<PagingData<LobstersPost>>,
  isPostSaved: (SavedPost) -> Boolean,
  postActions: PostActions,
  searchQuery: String,
  setSearchQuery: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val lazyPagingItems = items.collectAsLazyPagingItems()
  val triggerSearch = { query: String ->
    setSearchQuery(query)
    lazyPagingItems.refresh()
  }
  DisposableEffect(true) {
    // Clear search field when navigating away
    onDispose { setSearchQuery("") }
  }
  var searchActive by remember { mutableStateOf(false) }
  Column(
    modifier = modifier.semantics { isTraversalGroup = true }.zIndex(1f).fillMaxWidth(),
  ) {
    SearchBar(
      query = searchQuery,
      onQueryChange = setSearchQuery,
      onSearch = {
        triggerSearch(it)
        searchActive = true
      },
      active = searchActive,
      onActiveChange = { searchActive = it },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("search_bar"),
    ) {
      lazyPagingItems.itemSnapshotList.items.forEach { item ->
        val dbModel = item.toSavedPost()
        ListItem(
          item = dbModel,
          isSaved = isPostSaved,
          isRead = { false },
          postActions = postActions,
        )
        HorizontalDivider()
      }
    }
  }
}
