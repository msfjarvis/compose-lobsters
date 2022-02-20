package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.material.MaterialRichText
import dev.msfjarvis.claw.common.posts.PostDetails
import dev.msfjarvis.claw.common.posts.Submitter
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
fun ThemedRichText(
  modifier: Modifier = Modifier,
  content: @Composable RichTextScope.() -> Unit,
) {
  CompositionLocalProvider(
    LocalTextStyle provides MaterialTheme.typography.bodyLarge,
    LocalContentColor provides MaterialTheme.colorScheme.onBackground,
  ) { MaterialRichText(modifier) { content() } }
}

@Composable
fun CommentsHeader(
  postDetails: LobstersPostDetails,
) {
  val htmlConverter = LocalHTMLConverter.current
  Surface {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      PostDetails(
        post = postDetails.toDbModel(),
      )
      ThemedRichText { Markdown(htmlConverter.convertHTMLToMarkdown(postDetails.description)) }
    }
  }
}

@Composable
fun CommentEntry(
  comment: Comment,
) {
  val htmlConverter = LocalHTMLConverter.current
  Divider(color = Color.Gray.copy(0.4f))
  Row(modifier = Modifier.wrapContentHeight()) {
    Column(
      modifier =
        Modifier.padding(
          start = (comment.indentLevel * 12).dp,
          end = 8.dp,
          top = 4.dp,
          bottom = 4.dp
        )
    ) {
      Submitter(
        text = comment.user.username,
        avatarUrl = "https://lobste.rs/${comment.user.avatarUrl}",
        contentDescription = "User avatar for ${comment.user.username}",
      )
      ThemedRichText(modifier = Modifier.padding(top = 8.dp)) {
        Markdown(htmlConverter.convertHTMLToMarkdown(comment.comment))
      }
    }
  }
}
