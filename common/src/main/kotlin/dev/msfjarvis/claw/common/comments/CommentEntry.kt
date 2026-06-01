/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.onOk
import dev.msfjarvis.claw.common.R
import dev.msfjarvis.claw.common.comments.reply.plainTextFromHtml
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.PostTitle
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.posts.TagRow
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.ThemedRichText
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

private val ThreadIndentWidth = 10.dp
private val ThreadGuideOffset = 5.dp
private val ThreadGuideWidth = 1.5.dp

@Composable
internal fun CommentEntry(
  isExpanded: Boolean,
  commentNode: CommentNode,
  openUserProfile: (String) -> Unit,
  onToggleExpandedState: (String, Boolean) -> Unit,
  isLoggedIn: Boolean,
  upvoteComment: (String) -> Unit,
  unvoteComment: (String) -> Unit,
  onReply: (String, String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val comment = commentNode.comment
  var hasLocallyUpvoted by remember(comment.shortId) { mutableStateOf(comment.isUpvoted) }
  var isActionBarExpanded by
    remember(comment.shortId) { mutableStateOf(commentNode.indentLevel == 0) }
  val score =
    displayScore(
      score = comment.score,
      initiallyUpvoted = comment.isUpvoted,
      isUpvoted = hasLocallyUpvoted,
    )
  val theme = if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT
  val indentGuideLevel = commentNode.indentLevel.minus(1).coerceAtLeast(0)
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background)
        .drawBehind {
          if (indentGuideLevel > 0) {
            repeat(indentGuideLevel) { level ->
              val x = ThreadIndentWidth.toPx() * level + ThreadGuideOffset.toPx()
              drawLine(
                color =
                  CommentTreeColors.colorForDepth(
                    depth = level,
                    theme = theme,
                  ),
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = ThreadGuideWidth.toPx(),
                cap = StrokeCap.Round,
              )
            }
          }
        }
        .padding(start = ThreadIndentWidth * indentGuideLevel)
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .background(
            if (commentNode.isUnread) {
              MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.72f)
            } else {
              MaterialTheme.colorScheme.background
            }
          )
          .combinedClickable(
            onClick = {
              if (isExpanded) {
                if (isLoggedIn) isActionBarExpanded = !isActionBarExpanded
              } else {
                onToggleExpandedState(comment.shortId, true)
              }
            },
            onLongClick = {
              isActionBarExpanded = false
              onToggleExpandedState(comment.shortId, !isExpanded)
            },
          ),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Row(
        modifier =
          Modifier.fillMaxWidth()
            .padding(
              start = 16.dp,
              end = 16.dp,
              top = 12.dp,
              bottom = if (!isExpanded) 12.dp else 0.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = comment.user,
          style = MaterialTheme.typography.labelLarge,
          color =
            if (commentNode.isPostAuthor) MaterialTheme.colorScheme.tertiary
            else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.clickable { openUserProfile(comment.user) },
        )
        Spacer(Modifier.weight(1f))
        Text(
          text = score.toString(),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
          text = buildCommentAgeString(comment.timestamp, comment.edited),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      if (isExpanded) {
        ThemedRichText(
          text = comment.comment,
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        )
        AnimatedVisibility(
          visible = isActionBarExpanded,
          enter = expandVertically(expandFrom = Alignment.Top),
          exit = shrinkVertically(shrinkTowards = Alignment.Top),
        ) {
          CommentActionTray(
            isUpvoted = hasLocallyUpvoted,
            onVoteClick = {
              if (hasLocallyUpvoted) {
                unvoteComment(comment.shortId)
              } else {
                upvoteComment(comment.shortId)
              }
              hasLocallyUpvoted = !hasLocallyUpvoted
            },
            onReplyClick = {
              onReply(comment.shortId, plainTextFromHtml(comment.comment))
            },
          )
        }
      }
    }
  }
}

@Composable
private fun CommentActionTray(
  isUpvoted: Boolean,
  onVoteClick: () -> Unit,
  onReplyClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.End,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Outlined.KeyboardArrowUp,
      contentDescription = if (isUpvoted) "Remove upvote" else "Upvote",
      tint =
        if (isUpvoted) MaterialTheme.colorScheme.error
        else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.clickable(role = Role.Button, onClick = onVoteClick),
    )
    Spacer(Modifier.size(12.dp))
    Icon(
      imageVector = Icons.AutoMirrored.Outlined.Reply,
      contentDescription = "Reply",
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.clickable(role = Role.Button, onClick = onReplyClick),
    )
  }
}

@Composable
internal fun PreviewCommentEntry(commentNode: CommentNode) {
  LobstersTheme(darkTheme = true) {
    Box(Modifier.background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp)) {
      CommentEntry(
        isExpanded = true,
        commentNode = commentNode,
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

private fun displayScore(score: Int, initiallyUpvoted: Boolean, isUpvoted: Boolean): Int {
  return when {
    initiallyUpvoted == isUpvoted -> score
    isUpvoted -> score + 1
    else -> score - 1
  }
}

internal fun previewCommentNode(isUpvoted: Boolean = false) =
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
        user = "Alice",
        isUpvoted = isUpvoted,
      ),
    isPostAuthor = false,
    isUnread = true,
    indentLevel = 0,
  )

private fun buildCommentAgeString(timestamp: TemporalAccessor, edited: Boolean): String {
  val now = System.currentTimeMillis()
  val relativeTime =
    DateUtils.getRelativeTimeSpanString(
      Instant.from(timestamp).toEpochMilli(),
      now,
      DateUtils.MINUTE_IN_MILLIS,
      DateUtils.FORMAT_ABBREV_RELATIVE,
    )
  return if (edited) "$relativeTime · edited" else relativeTime.toString()
}
