/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.posts.TEST_POST_ACTIONS
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.UIPost
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun LobstersListItem(item: UIPost, postActions: PostActions, modifier: Modifier = Modifier) {
  val commentsAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.AutoMirrored.Filled.Reply),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = { postActions.viewCommentsPage(item) },
    )
  val shareAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.Filled.Share),
      background = MaterialTheme.colorScheme.tertiary,
      onSwipe = { postActions.share(item) },
    )
  SwipeableActionsBox(
    startActions = listOf(shareAction),
    endActions = listOf(commentsAction),
    swipeThreshold = 80.dp,
    backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.surfaceVariant,
  ) {
    LobstersCard(post = item, postActions = postActions, modifier = modifier)
  }
}

@ThemePreviews
@Composable
private fun ItemPreview() {
  LobstersTheme { LobstersListItem(item = TEST_POST, postActions = TEST_POST_ACTIONS) }
}
