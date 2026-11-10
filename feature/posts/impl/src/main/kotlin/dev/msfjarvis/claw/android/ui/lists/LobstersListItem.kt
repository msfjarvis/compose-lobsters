/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.UIPost
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun LobstersListItem(
  item: UIPost,
  isSaved: Boolean,
  isRead: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val webAction =
    SwipeAction(
      icon = rememberVectorPainter(Icons.Filled.Web),
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
    endActions = listOf(webAction),
    swipeThreshold = 80.dp,
    backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.surfaceVariant,
  ) {
    LobstersCard(
      post = item,
      isSaved = isSaved,
      isRead = isRead,
      onPostClick = { postActions.viewPost(it.shortId, it.url, it.commentsUrl) },
      onCommentsClick = { postActions.viewComments(it.shortId, it.commentsUrl) },
      onSaveClick = postActions::toggleSave,
      modifier = modifier,
    )
  }
}

@ThemePreviews
@Composable
private fun ItemPreview() {
  LobstersTheme {
    LobstersListItem(
      item = previewPosts(1).first(),
      isSaved = true,
      isRead = true,
      postActions = TEST_POST_ACTIONS,
    )
  }
}
