/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun LobstersListItem(
  item: SavedPost,
  isSaved: (SavedPost) -> Boolean,
  isRead: suspend (String) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val read by produceState(false, item.shortId) { value = isRead(item.shortId) }
  var saved by remember(item.shortId) { mutableStateOf(isSaved(item)) }
  val commentsAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Reply),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = { postActions.viewCommentsPage(item.commentsUrl) },
    )
  val saveAction =
    SwipeAction(
      icon =
        rememberVectorPainter(if (saved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = {
        postActions.toggleSave(item)
        saved = isSaved(item)
      },
    )
  SwipeableActionsBox(
    startActions = listOf(saveAction),
    endActions = listOf(commentsAction),
  ) {
    LobstersCard(
      post = item,
      isSaved = saved,
      isRead = read,
      viewComments = postActions::viewComments,
      toggleSave = { post ->
        postActions.toggleSave(post)
        saved = isSaved(post)
      },
      viewPost = postActions::viewPost,
      modifier = modifier,
    )
  }
}
