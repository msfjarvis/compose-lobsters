/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.onOk
import dev.msfjarvis.claw.common.BuildConfig
import dev.msfjarvis.claw.common.R
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.PostTitle
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.posts.TagRow
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import java.time.Instant
import java.time.temporal.TemporalAccessor
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun CommentsHeader(
  post: UIPost,
  postActions: PostActions,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val linkMetadata by
    produceState(initialValue = LinkMetadata(post.url, null), key1 = post) {
      runSuspendCatching { postActions.getLinkMetadata(post.url) }
        .onOk { metadata -> value = metadata }
    }

  Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
    Column(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      PostTitle(title = post.title, isRead = false)
      TagRow(tags = post.tags.toImmutableList())
      Spacer(Modifier.height(4.dp))

      if (linkMetadata.url.isNotBlank()) {
        PostLink(
          linkMetadata = linkMetadata,
          modifier =
            Modifier.clickable {
              postActions.viewPost(post.shortId, linkMetadata.url, post.commentsUrl)
            },
        )
        Spacer(Modifier.height(4.dp))
      }

      if (post.description.isNotBlank()) {
        ThemedRichText(post.description)
        Spacer(Modifier.height(4.dp))
      }
      Submitter(
        text =
          AnnotatedString(
            "${if (post.userIsAuthor) "Authored" else "Submitted"} by ${post.submitter}"
          ),
        avatarUrl = "https://lobste.rs/avatars/${post.submitter}-100.png",
        contentDescription = stringResource(R.string.user_avatar_for, post.submitter),
        modifier = Modifier.clickable { openUserProfile(post.submitter) },
      )
    }
  }
}

@Composable
private fun PostLink(linkMetadata: LinkMetadata, modifier: Modifier = Modifier) {
  Box(
    modifier.background(
      color = MaterialTheme.colorScheme.secondary,
      shape = RoundedCornerShape(8.dp),
    )
  ) {
    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      NetworkImage(
        url = linkMetadata.faviconUrl,
        placeholder = Icons.Filled.Public,
        contentDescription = "",
        modifier = Modifier.size(24.dp),
      )
      Text(
        text = linkMetadata.url,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSecondary,
        style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

@Composable
internal fun CommentEntry(
  isExpanded: Boolean,
  commentNode: CommentNode,
  openUserProfile: (String) -> Unit,
  onToggleExpandedState: (String, Boolean) -> Unit,
  isLoggedIn: Boolean,
  upvoteComment: (String) -> Unit,
  unvoteComment: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val comment = commentNode.comment
  var hasLocallyUpvoted by remember(comment.shortId) { mutableStateOf(comment.isUpvoted) }
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .background(
          if (commentNode.isUnread) MaterialTheme.colorScheme.surfaceContainerHigh
          else MaterialTheme.colorScheme.background
        )
        .padding(
          start = CommentEntryPadding * commentNode.indentLevel,
          end = CommentEntryPadding,
          top = CommentEntryPadding,
          bottom = CommentEntryPadding,
        )
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        CommentExpandToggle(
          isExpanded = isExpanded,
          onClick = { onToggleExpandedState(comment.shortId, !isExpanded) },
          modifier = Modifier.padding(end = 8.dp),
        )
        if (isLoggedIn && BuildConfig.DEBUG) {
          CommentVoteChip(
            score =
              displayScore(
                score = comment.score,
                initiallyUpvoted = comment.isUpvoted,
                isUpvoted = hasLocallyUpvoted,
              ),
            isUpvoted = hasLocallyUpvoted,
            modifier = Modifier.padding(end = 8.dp),
            onClick = {
              if (hasLocallyUpvoted) {
                unvoteComment(comment.shortId)
              } else {
                upvoteComment(comment.shortId)
              }
              hasLocallyUpvoted = !hasLocallyUpvoted
            },
          )
        }
        Submitter(
          text =
            buildCommenterString(
              commenterName = comment.user,
              score = comment.score,
              timestamp = comment.timestamp,
              edited = comment.edited,
              nameColorOverride =
                if (commentNode.isPostAuthor) MaterialTheme.colorScheme.tertiary else null,
            ),
          avatarUrl = "https://lobste.rs/avatars/${comment.user}-100.png",
          contentDescription = stringResource(R.string.user_avatar_for, comment.user),
          modifier = Modifier.clickable { openUserProfile(comment.user) },
        )
      }
      if (isExpanded) {
        ThemedRichText(text = comment.comment, modifier = Modifier.padding(top = 8.dp))
      }
    }
  }
}

@Composable
private fun CommentExpandToggle(
  isExpanded: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Text(
    text = if (isExpanded) "[-]" else "[+]",
    style = MaterialTheme.typography.labelMedium,
    color = MaterialTheme.colorScheme.primary,
    modifier = modifier.clickable(role = Role.Button, onClick = onClick),
  )
}

@Composable
private fun CommentVoteChip(
  score: Int,
  isUpvoted: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .background(
          color =
            if (isUpvoted) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(999.dp),
        )
        .clickable(role = Role.Button, onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = "↑",
      style = MaterialTheme.typography.labelMedium,
      color =
        if (isUpvoted) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = score.toString(),
      style = MaterialTheme.typography.labelMedium,
      color =
        if (isUpvoted) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@ThemePreviews
@Composable
private fun CommentEntryPreview() {
  PreviewCommentEntry(previewCommentNode())
}

@ThemePreviews
@Preview(name = "Upvoted")
@Composable
private fun CommentEntryUpvotedPreview() {
  PreviewCommentEntry(previewCommentNode(isUpvoted = true))
}

@Composable
private fun PreviewCommentEntry(commentNode: CommentNode) {
  LobstersTheme {
    Box(Modifier.background(MaterialTheme.colorScheme.background).padding(16.dp)) {
      CommentEntry(
        isExpanded = true,
        commentNode = commentNode,
        openUserProfile = {},
        onToggleExpandedState = { _, _ -> },
        isLoggedIn = true,
        upvoteComment = {},
        unvoteComment = {},
      )
    }
  }
}

private fun displayScore(score: Int, initiallyUpvoted: Boolean, isUpvoted: Boolean): Int {
  return when {
    initiallyUpvoted == isUpvoted -> score
    isUpvoted -> score + 1
    else -> score - 1
  }
}

private fun previewCommentNode(isUpvoted: Boolean = false) =
  CommentNode(
    comment =
      Comment(
        shortId = "preview-comment",
        comment =
          "<p>This is a preview comment with enough content to evaluate spacing, metadata, and future vote affordances.</p>",
        score = 42,
        timestamp = Instant.now(),
        edited = false,
        parentComment = null,
        user = "alice",
        isUpvoted = isUpvoted,
      ),
    isPostAuthor = false,
    isUnread = true,
    indentLevel = 0,
  )

private val CommentEntryPadding = 16f.dp

private fun buildCommenterString(
  commenterName: String,
  score: Int,
  timestamp: TemporalAccessor,
  edited: Boolean,
  nameColorOverride: Color? = null,
): AnnotatedString {
  val now = System.currentTimeMillis()
  val relativeTime =
    DateUtils.getRelativeTimeSpanString(
      Instant.from(timestamp).toEpochMilli(),
      now,
      DateUtils.MINUTE_IN_MILLIS,
    )
  return buildAnnotatedString {
    if (nameColorOverride != null) {
      withStyle(SpanStyle(color = nameColorOverride)) { append(commenterName) }
    } else {
      append(commenterName)
    }
    append(' ')
    append('•')
    append(' ')
    append("$score points")
    append(' ')
    append('•')
    append(' ')
    append(relativeTime.toString())
    if (edited) {
      append(' ')
      append('(')
      append("Edited")
      append(')')
    }
  }
}
