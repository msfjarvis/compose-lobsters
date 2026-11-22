/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalCoilApi::class)
@PreviewTest
@Preview(name = "compact", widthDp = 360, heightDp = 800)
@Preview(name = "medium", widthDp = 720, heightDp = 900)
@Preview(name = "expanded", widthDp = 1200, heightDp = 800)
@Preview(name = "short-large-font", widthDp = 900, heightDp = 480, fontScale = 1.5f)
@Composable
private fun FeedScreenScreenshot() {
  val items = MutableStateFlow(PagingData.from(previewPosts(8))).collectAsLazyPagingItems()
  val imageHandler = AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(null) }
  CompositionLocalProvider(LocalAsyncImagePreviewHandler provides imageHandler) {
    LobstersTheme {
      NetworkPosts(
        lazyPagingItems = items,
        listState = rememberLazyListState(),
        postActions = TEST_POST_ACTIONS,
        contentPadding = PaddingValues(),
        filteredTags = persistentSetOf(),
      )
    }
  }
}

@OptIn(ExperimentalCoilApi::class)
@PreviewTest
@Preview(name = "compact", widthDp = 360, heightDp = 800)
@Preview(name = "expanded", widthDp = 1200, heightDp = 800)
@Composable
private fun SavedPostsScreenshot() {
  val imageHandler = AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(null) }
  CompositionLocalProvider(LocalAsyncImagePreviewHandler provides imageHandler) {
    LobstersTheme {
      DatabasePosts(
        items = persistentMapOf("January 2026" to previewPosts(8)),
        listState = rememberLazyListState(),
        postActions = TEST_POST_ACTIONS,
        contentPadding = PaddingValues(),
      )
    }
  }
}
