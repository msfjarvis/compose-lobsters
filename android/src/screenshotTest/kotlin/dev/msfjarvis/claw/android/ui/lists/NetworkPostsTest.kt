/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.posts.TEST_POST_ACTIONS
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import kotlinx.coroutines.flow.MutableStateFlow

@SuppressLint("ComposePreviewPublic", "ComposeUnstableReceiver")
class NetworkPostsTest {
  @ThemePreviews
  @Composable
  fun DefaultPreview() {
    val items = List(20) { TEST_POST.copy(shortId = "${TEST_POST.shortId}${it}") }
    val flow = MutableStateFlow(PagingData.from(items))
    LobstersTheme {
      NetworkPosts(
        lazyPagingItems = flow.collectAsLazyPagingItems(),
        listState = rememberLazyListState(),
        postActions = TEST_POST_ACTIONS,
        contentPadding = PaddingValues(),
        onPostClick = {}
      )
    }
  }
}
