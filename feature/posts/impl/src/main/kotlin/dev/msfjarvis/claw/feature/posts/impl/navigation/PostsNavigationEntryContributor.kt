/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.feature.posts.impl.R
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.flow.Flow

/** Adds the feed and saved-post entries owned by the posts feature. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun postsNavigationEntryContributor(
  hottestPosts: Flow<PagingData<UIPost>>,
  newestPosts: Flow<PagingData<UIPost>>,
  hottestListState: LazyListState,
  newestListState: LazyListState,
  savedPosts: ImmutableMap<String, List<UIPost>>,
  savedListState: LazyListState,
  postActions: PostActions,
  filteredTags: ImmutableSet<String>,
  contentPadding: PaddingValues,
): NavigationEntryContributor = NavigationEntryContributor {
  entry<Hottest>(
    metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
  ) {
    NetworkPosts(
      lazyPagingItems = hottestPosts.collectAsLazyPagingItems(),
      listState = hottestListState,
      postActions = postActions,
      contentPadding = contentPadding,
      filteredTags = filteredTags,
    )
  }
  entry<Newest>(
    metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
  ) {
    NetworkPosts(
      lazyPagingItems = newestPosts.collectAsLazyPagingItems(),
      listState = newestListState,
      postActions = postActions,
      contentPadding = contentPadding,
      filteredTags = filteredTags,
    )
  }
  entry<Saved>(metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })) {
    DatabasePosts(
      items = savedPosts,
      listState = savedListState,
      postActions = postActions,
      contentPadding = contentPadding,
    )
  }
}

@Composable
private fun Placeholder() {
  Text(stringResource(R.string.no_post_selected))
}
