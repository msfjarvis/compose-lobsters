/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.UIPost
import java.time.Instant
import java.time.temporal.TemporalAccessor

private const val AnimationDuration = 100

@Composable
internal fun CommentsPageInternal(
  details: UIPost,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  commentState: PostComments?,
  markSeenComments: (String, List<Comment>) -> Unit,
  openUserProfile: (String) -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
) {
  val commentsHandler = CommentsHandler()
  LaunchedEffect(key1 = details, key2 = commentState) {
    commentsHandler.createListNode(details.comments, commentState) { comment ->
      details.userIsAuthor && comment.user == details.submitter
    }
  }

  val onToggleExpandedState = { shortId: String, isExpanded: Boolean ->
    commentsHandler.updateListNode(shortId, isExpanded)
  }

  val context = LocalContext.current
  val commentNodes by commentsHandler.listItems.collectAsStateWithLifecycle()
  val commentListState = rememberLazyListState()

  LaunchedEffect(key1 = details, key2 = commentState) {
    if (details.comments.isNotEmpty() && !commentState?.commentIds.isNullOrEmpty()) {
      val unreadCount = details.comments.size - (commentState?.commentIds?.size ?: 0)
      if (unreadCount > 0) {
        val text =
          when (unreadCount) {
            1 -> "$unreadCount unread comment"
            else -> "$unreadCount unread comments"
          }
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
      }
    }
    markSeenComments(details.shortId, details.comments)
  }

  Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding, state = commentListState) {
      item {
        CommentsHeader(
          post = details,
          postActions = postActions,
          htmlConverter = htmlConverter,
          openUserProfile = openUserProfile,
        )
      }

      if (commentNodes.isNotEmpty()) {
        item(key = "comments_header") {
          Text(
            text = "Comments",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          )
        }

        items(items = commentNodes, key = { node -> node.comment.shortId }) { node ->
          Node(node, htmlConverter, openUserProfile, onToggleExpandedState)
        }

        item(key = "bottom_spacer") {
          Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }
      } else {
        item {
          Text(
            text = "No Comments",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

/**
 * Simple tree view implementation by Anton Shilov who was smarter in 2020 than I am today
 * https://gist.github.com/antonshilov/ef8cd0a360a5cc0f823b2a4e85084720
 */
@Composable
private fun Node(
  node: CommentNode,
  htmlConverter: HTMLConverter,
  openUserProfile: (String) -> Unit,
  onToggleExpandedState: (String, Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    NodeBox(
      node = node,
      isExpanded = node.isExpanded,
      htmlConverter = htmlConverter,
      openUserProfile,
      modifier =
        modifier.clickable(
          onClick = { onToggleExpandedState(node.comment.shortId, !node.isExpanded) }
        ),
    )

    AnimatedVisibility(
      visible = node.isExpanded,
      enter = fadeIn(tween(AnimationDuration)) + expandVertically(tween(AnimationDuration)),
      exit = fadeOut(tween(AnimationDuration)) + shrinkVertically(tween(AnimationDuration)),
    ) {
      Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        node.children.forEach { model ->
          Node(model, htmlConverter, openUserProfile, onToggleExpandedState)
        }
      }
    }
  }
}

@Composable
private fun NodeBox(
  node: CommentNode,
  isExpanded: Boolean,
  htmlConverter: HTMLConverter,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  CommentEntry(isExpanded, node, htmlConverter, openUserProfile, modifier)
  HorizontalDivider()
}

private val CommentEntryPadding = 16f.dp

@Composable
private fun CommentEntry(
  isExpanded: Boolean,
  commentNode: CommentNode,
  htmlConverter: HTMLConverter,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val comment = commentNode.comment
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
      Submitter(
        text =
          buildCommenterString(
            commenterName = comment.user,
            score = comment.score,
            createdAt = comment.createdAt,
            updatedAt = comment.lastEditedAt,
            nameColorOverride =
              if (commentNode.isPostAuthor) MaterialTheme.colorScheme.tertiary else null,
          ),
        avatarUrl = "https://lobste.rs/avatars/${comment.user}-100.png",
        contentDescription = "User avatar for ${comment.user}",
        modifier = Modifier.clickable { openUserProfile(comment.user) },
      )
      if (isExpanded) {
        ThemedRichText(
          text = htmlConverter.convertHTMLToMarkdown(comment.comment),
          modifier = Modifier.padding(top = 8.dp),
        )
      }
    }
  }
}

private fun buildCommenterString(
  commenterName: String,
  score: Int,
  createdAt: TemporalAccessor,
  updatedAt: TemporalAccessor,
  nameColorOverride: Color? = null,
): AnnotatedString {
  val now = System.currentTimeMillis()
  val createdRelative =
    DateUtils.getRelativeTimeSpanString(
      Instant.from(createdAt).toEpochMilli(),
      now,
      DateUtils.MINUTE_IN_MILLIS,
    )
  val updatedRelative =
    DateUtils.getRelativeTimeSpanString(
      Instant.from(updatedAt).toEpochMilli(),
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
    append(createdRelative.toString())
    if (updatedRelative != createdRelative) {
      append(' ')
      append('(')
      append("Updated")
      append(' ')
      append(updatedRelative.toString())
      append(')')
    }
  }
}
