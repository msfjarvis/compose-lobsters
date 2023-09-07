/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun ListItem(
  item: SavedPost,
  isSaved: suspend (SavedPost) -> Boolean,
  isRead: suspend (String) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val saved by produceState(false, item) { value = isSaved(item) }
  val read by produceState(false, item.shortId) { value = isRead(item.shortId) }
  val commentsAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Reply),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = { postActions.viewCommentsPage(item.commentsUrl) },
    )
  SwipeableActionsBox(
    endActions = listOf(commentsAction),
  ) {
    LobstersCard(
      post = item,
      isSaved = saved,
      isRead = read,
      postActions = postActions,
      modifier = modifier,
    )
  }
}
