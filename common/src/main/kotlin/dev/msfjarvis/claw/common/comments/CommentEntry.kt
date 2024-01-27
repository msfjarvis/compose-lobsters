/*
 * Copyright © 2021-2024 Harsh Shandilya.
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.onSuccess
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.PostTitle
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.posts.TagRow
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import java.time.Instant
import java.time.temporal.TemporalAccessor
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun CommentsHeader(
  post: UIPost,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  modifier: Modifier = Modifier,
) {
  val uriHandler = LocalUriHandler.current
  val linkMetadata by
    produceState(initialValue = LinkMetadata(post.url, null)) {
      runSuspendCatching { postActions.getLinkMetadata(post.url) }
        .onSuccess { metadata -> value = metadata }
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
        ThemedRichText(htmlConverter.convertHTMLToMarkdown(post.description))
        Spacer(Modifier.height(4.dp))
      }
      Submitter(
        text = AnnotatedString("Submitted by ${post.submitter.username}"),
        avatarUrl = "https://lobste.rs/${post.submitter.avatarUrl}",
        contentDescription = "User avatar for ${post.submitter.username}",
        modifier =
          Modifier.clickable {
            uriHandler.openUri("https://lobste.rs/u/${post.submitter.username}")
          },
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

private val CommentEntryPadding = 16f.dp

@Composable
internal fun CommentEntry(
  commentNode: CommentNode,
  htmlConverter: HTMLConverter,
  toggleExpanded: (CommentNode) -> Unit,
  modifier: Modifier = Modifier,
) {
  val uriHandler = LocalUriHandler.current
  val comment = commentNode.comment
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .clickable { toggleExpanded(commentNode) }
        .background(MaterialTheme.colorScheme.background)
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
            commenterName = comment.user.username,
            score = comment.score,
            isUnread = commentNode.isUnread,
            createdAt = comment.createdAt,
            updatedAt = comment.updatedAt,
          ),
        avatarUrl = "https://lobste.rs/${comment.user.avatarUrl}",
        contentDescription = "User avatar for ${comment.user.username}",
        modifier =
          Modifier.clickable { uriHandler.openUri("https://lobste.rs/u/${comment.user.username}") },
      )
      if (commentNode.isExpanded) {
        ThemedRichText(
          text = htmlConverter.convertHTMLToMarkdown(comment.comment),
          modifier = Modifier.padding(top = 8.dp),
        )
      }
    }
  }
}

@Composable
fun buildCommenterString(
  commenterName: String,
  score: Int,
  isUnread: Boolean,
  createdAt: TemporalAccessor,
  updatedAt: TemporalAccessor,
): AnnotatedString {
  val now = System.currentTimeMillis()
  val createdRelative =
    remember(createdAt) {
      DateUtils.getRelativeTimeSpanString(
        Instant.from(createdAt).toEpochMilli(),
        now,
        DateUtils.MINUTE_IN_MILLIS,
      )
    }
  val updatedRelative =
    remember(updatedAt) {
      DateUtils.getRelativeTimeSpanString(
        Instant.from(updatedAt).toEpochMilli(),
        now,
        DateUtils.MINUTE_IN_MILLIS,
      )
    }
  return buildAnnotatedString {
    append(commenterName)
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
    if (isUnread) {
      append(' ')
      withStyle(
        style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
      ) {
        append("(unread)")
      }
    }
  }
}
