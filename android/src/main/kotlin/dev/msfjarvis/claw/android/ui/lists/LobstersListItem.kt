/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.UIPost
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun LobstersListItem(
  item: UIPost,
  postActions: PostActions,
  refresh: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val commentsAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Reply),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = { postActions.viewCommentsPage(item) },
    )
  SwipeableActionsBox(endActions = listOf(commentsAction)) {
    LobstersCard(post = item, postActions = postActions, refresh = refresh, modifier = modifier)
  }
}
