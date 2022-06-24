package dev.msfjarvis.claw.common.comments

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.PostTitle
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.posts.TagRow
import dev.msfjarvis.claw.common.res.ClawIcons
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
fun CommentsHeader(
  postDetails: LobstersPostDetails,
  postActions: PostActions,
) {
  val htmlConverter = LocalHTMLConverter.current
  val uriHandler = LocalUriHandler.current

  Surface(color = MaterialTheme.colorScheme.background) {
    Column(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      PostTitle(title = postDetails.title)
      TagRow(tags = postDetails.tags)
      Spacer(Modifier.height(4.dp))

      if (postDetails.url.isNotBlank()) {
        PostLink(
          link = postDetails.url,
          modifier =
            Modifier.clickable { postActions.viewPost(postDetails.url, postDetails.commentsUrl) },
        )
        Spacer(Modifier.height(4.dp))
      }

      if (postDetails.description.isNotBlank()) {
        ThemedRichText(htmlConverter.convertHTMLToMarkdown(postDetails.description))
        Spacer(Modifier.height(4.dp))
      }
      Submitter(
        text = "Submitted by ${postDetails.submitter.username}",
        avatarUrl = "https://lobste.rs/${postDetails.submitter.avatarUrl}",
        contentDescription = "User avatar for ${postDetails.submitter.username}",
        modifier =
          Modifier.clickable {
            uriHandler.openUri("https://lobste.rs/u/${postDetails.submitter.username}")
          },
      )
    }
  }
}

@Composable
fun PostLink(
  link: String,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier.background(
      color = MaterialTheme.colorScheme.secondary,
      shape = RoundedCornerShape(8.dp),
    )
  ) {
    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
      Icon(
        painter = ClawIcons.Web,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSecondary,
      )
      Text(
        text = link,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = MaterialTheme.colorScheme.onSecondary,
        style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CommentEntry(
  comment: Comment,
) {
  var expanded by remember(comment) { mutableStateOf(true) }
  val htmlConverter = LocalHTMLConverter.current
  val uriHandler = LocalUriHandler.current
  Box(
    modifier =
      Modifier.fillMaxWidth()
        .clickable { expanded = !expanded }
        .background(MaterialTheme.colorScheme.background)
        .padding(start = (comment.indentLevel * 16).dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Submitter(
        text = comment.user.username,
        avatarUrl = "https://lobste.rs/${comment.user.avatarUrl}",
        contentDescription = "User avatar for ${comment.user.username}",
        modifier =
          Modifier.clickable { uriHandler.openUri("https://lobste.rs/u/${comment.user.username}") },
      )
      AnimatedContent(targetState = expanded) { expandedState ->
        if (expandedState) {
          ThemedRichText(
            text = htmlConverter.convertHTMLToMarkdown(comment.comment),
            modifier = Modifier.padding(top = 8.dp)
          )
        }
      }
    }
  }
}
