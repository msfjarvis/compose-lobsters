/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.ui.decorations.MonthHeader
import dev.msfjarvis.claw.feature.posts.impl.R
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableMap

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DatabasePosts(
  items: ImmutableMap<String, List<UIPost>>,
  listState: LazyListState,
  postActions: PostActions,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  ReportDrawn()
  Box(modifier = modifier.fillMaxSize()) {
    if (items.isEmpty()) {
      Column(
        modifier = Modifier.align(Alignment.Center).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.Inbox,
          contentDescription = null,
          modifier = Modifier.align(Alignment.CenterHorizontally).size(36.dp),
        )
        Text(
          text = stringResource(R.string.no_saved_posts),
          style = MaterialTheme.typography.headlineSmall,
        )
        Text(
          text = stringResource(R.string.saved_posts_hint),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    } else {
      LazyColumn(
        state = listState,
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize(),
      ) {
        items.forEach { (month, posts) ->
          stickyHeader(contentType = "month-header") {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
              MonthHeader(
                label = month,
                modifier = Modifier.widthIn(max = FeedMaxWidth).fillMaxWidth(),
              )
            }
          }
          items(items = posts, key = { it.shortId }, contentType = { "LobstersItem" }) { item ->
            val isSaved = postActions.isPostSaved(item)
            val isRead = postActions.isPostRead(item)
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
              Column(Modifier.widthIn(max = FeedMaxWidth).fillMaxWidth()) {
                LobstersListItem(
                  item = item,
                  isSaved = isSaved,
                  isRead = isRead,
                  postActions = postActions,
                )
                HorizontalDivider()
              }
            }
          }
        }
      }
    }
  }
}
