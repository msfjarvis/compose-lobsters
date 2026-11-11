/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.discussion.impl.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.NavKey
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Reply
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.reply.ReplyScreen
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun discussionNavigationEntryContributor(
  postActions: PostActions,
  contentPadding: PaddingValues,
  openUserProfile: (String) -> Unit,
  navigate: (NavKey, Boolean) -> Unit,
  onReplySubmitted: () -> Unit,
): NavigationEntryContributor = NavigationEntryContributor {
  entry<Comments>(metadata = ListDetailSceneStrategy.detailPane()) { destination ->
    CommentsPage(
      postId = destination.postId,
      commentsUrl = destination.commentsUrl,
      postActions = postActions,
      contentPadding = contentPadding,
      openUserProfile = openUserProfile,
      openReplyScreen = { postId, commentId, commentText ->
        navigate(Reply(postId, commentId, commentText), true)
      },
    )
  }
  entry<Reply>(metadata = ListDetailSceneStrategy.extraPane()) { destination ->
    ReplyScreen(
      postId = destination.postId,
      commentId = destination.commentId,
      commentText = destination.commentText,
      onReplySubmitted = onReplySubmitted,
      contentPadding = contentPadding,
    )
  }
}
