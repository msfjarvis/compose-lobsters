/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.Comment
import kotlin.time.Clock

@PreviewTest
@DevicePreviews
@ThemePreviews
@Composable
private fun CommentEntryPreview(
  @PreviewParameter(CommentEntryPreviewParameterProvider::class)
  params: CommentEntryPreviewParameters
) {
  LobstersTheme(darkTheme = true) {
    Box(Modifier.background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp)) {
      CommentEntry(
        isExpanded = params.isExpanded,
        commentNode =
          previewCommentNode(isUpvoted = params.isUpvoted, hasChildren = params.hasChildren),
        openUserProfile = {},
        onToggleExpandedState = { _, _ -> },
        isLoggedIn = true,
        upvoteComment = {},
        unvoteComment = {},
        onReply = { _, _ -> },
      )
    }
  }
}

private class CommentEntryPreviewParameters(
  val isUpvoted: Boolean,
  val hasChildren: Boolean,
  val isExpanded: Boolean,
)

private class CommentEntryPreviewParameterProvider :
  PreviewParameterProvider<CommentEntryPreviewParameters> {
  override val values: Sequence<CommentEntryPreviewParameters> =
    sequenceOf(
      CommentEntryPreviewParameters(isUpvoted = true, hasChildren = false, isExpanded = true),
      CommentEntryPreviewParameters(isUpvoted = false, hasChildren = false, isExpanded = true),
      CommentEntryPreviewParameters(isUpvoted = true, hasChildren = true, isExpanded = true),
      CommentEntryPreviewParameters(isUpvoted = false, hasChildren = true, isExpanded = false),
    )

  override fun getDisplayName(index: Int): String {
    return values.toList()[index].toString()
  }
}

private fun previewCommentNode(isUpvoted: Boolean = false, hasChildren: Boolean = false) =
  CommentNode(
      comment =
        Comment(
          shortId = "preview-comment",
          comment =
            "<p>This is a preview comment with enough content to evaluate spacing, metadata, and future vote affordances.</p>",
          score = 42,
          timestamp = Clock.System.now(),
          edited = false,
          parentComment = null,
          user = "Alice",
          isUpvoted = isUpvoted,
        ),
      isPostAuthor = false,
      isUnread = true,
      indentLevel = 0,
    )
    .also { parent ->
      if (hasChildren) {
        parent.addChild(
          CommentNode(
            comment =
              Comment(
                shortId = "preview-child-comment",
                comment =
                  "<p>This child comment helps preview expanded and collapsed thread states.</p>",
                score = 7,
                timestamp = Clock.System.now(),
                edited = false,
                parentComment = parent.comment.shortId,
                user = "Bob",
                isUpvoted = false,
              ),
            isPostAuthor = false,
            isUnread = false,
            indentLevel = 1,
          )
        )
      }
    }
