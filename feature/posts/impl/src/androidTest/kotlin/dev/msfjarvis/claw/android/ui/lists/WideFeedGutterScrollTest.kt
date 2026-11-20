/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.common.theme.LobstersTheme
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoilApi::class)
class WideFeedGutterScrollTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun networkFeedScrollsWhenDraggedInWideViewportGutter() {
    val posts = previewPosts(30)
    val pagingData = MutableStateFlow(PagingData.from(posts))
    lateinit var listState: androidx.compose.foundation.lazy.LazyListState

    compose.setContent {
      CompositionLocalProvider(
        LocalInspectionMode provides true,
        LocalAsyncImagePreviewHandler provides
          AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(null) },
      ) {
        LobstersTheme {
          val lazyPagingItems = pagingData.collectAsLazyPagingItems()
          listState = rememberLazyListState()
          NetworkPosts(
            lazyPagingItems = lazyPagingItems,
            listState = listState,
            postActions = TEST_POST_ACTIONS,
            contentPadding = PaddingValues(),
            filteredTags = persistentSetOf(),
            modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }

    dragInWideGutter()

    compose.runOnIdle { assertThat(listState.firstVisibleItemIndex).isGreaterThan(0) }
  }

  @Test
  fun savedPostsScrollWhenDraggedInWideViewportGutter() {
    val posts = previewPosts(30)
    lateinit var listState: androidx.compose.foundation.lazy.LazyListState

    compose.setContent {
      CompositionLocalProvider(
        LocalInspectionMode provides true,
        LocalAsyncImagePreviewHandler provides
          AsyncImagePreviewHandler { _, _ -> AsyncImagePainter.State.Loading(null) },
      ) {
        LobstersTheme {
          listState = rememberLazyListState()
          DatabasePosts(
            items = persistentMapOf("January 2026" to posts),
            listState = listState,
            postActions = TEST_POST_ACTIONS,
            contentPadding = PaddingValues(),
            modifier = Modifier.fillMaxSize(),
          )
        }
      }
    }

    dragInWideGutter()

    compose.runOnIdle { assertThat(listState.firstVisibleItemIndex).isGreaterThan(0) }
  }

  private fun dragInWideGutter() {
    val viewport = compose.onRoot().fetchSemanticsNode().boundsInRoot
    val widthDp = with(compose.density) { viewport.width.toDp() }
    assumeTrue("Requires a wide viewport", widthDp.value >= 1_000f)

    val contentWidth = with(compose.density) { FeedMaxWidth.toPx() }
    val gutterX = (viewport.width * 3f + contentWidth) / 4f
    val startY = viewport.height * 0.8f
    val endY = viewport.height * 0.2f
    compose.onRoot().performTouchInput {
      swipe(
        start = Offset(gutterX, startY),
        end = Offset(gutterX, endY),
        durationMillis = 500,
      )
    }
  }
}
