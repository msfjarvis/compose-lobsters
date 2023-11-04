/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.SearchBar
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchList(
  items: Flow<PagingData<LobstersPost>>,
  listState: LazyListState,
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
  Column(modifier = modifier.semantics { isTraversalGroup = true }.zIndex(1f).fillMaxWidth()) {
    SearchBar(
      value = searchQuery,
      onValueChange = setSearchQuery,
      onSearch = {
        triggerSearch(it)
        searchActive = true
      },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).testTag("search_bar"),
    )
    if (searchActive) {
      NetworkPosts(
        lazyPagingItems = lazyPagingItems,
        listState = listState,
        isPostSaved = isPostSaved,
        isPostRead = { false },
        postActions = postActions,
      )
    } else {
      Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
          Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = "No search results",
            modifier = Modifier.align(Alignment.CenterHorizontally).size(36.dp),
          )
          Text(text = "Nothing to see here", style = MaterialTheme.typography.headlineSmall)
        }
      }
    }
  }
}
