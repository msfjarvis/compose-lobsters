/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.decorations.MonthHeader
import dev.msfjarvis.claw.database.local.SavedPost
import java.time.Month
import kotlinx.collections.immutable.ImmutableMap

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatabasePosts(
  items: ImmutableMap<Month, List<SavedPost>>,
  listState: LazyListState,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  ReportDrawn()
  Box(modifier = modifier.fillMaxSize()) {
    if (items.isEmpty()) {
      Column(modifier = Modifier.align(Alignment.Center)) {
        Icon(
          imageVector = Icons.Filled.Inbox,
          contentDescription = "Empty inbox icon",
          modifier = Modifier.align(Alignment.CenterHorizontally).size(36.dp),
        )
        Text(text = "No saved posts", style = MaterialTheme.typography.headlineSmall)
      }
    } else {
      LazyColumn(state = listState) {
        items.forEach { (month, posts) ->
          stickyHeader(contentType = "month-header") { MonthHeader(month = month) }
          items(
            items = posts,
            contentType = { "LobstersItem" },
          ) { item ->
            ListItem(
              item = item,
              isSaved = { true },
              postActions = postActions,
            )
            Divider()
          }
        }
      }
    }
  }
}
