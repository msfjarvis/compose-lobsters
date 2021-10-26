package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material.MaterialRichText
import dev.msfjarvis.claw.common.posts.PostDetails
import dev.msfjarvis.claw.common.posts.SubmitterName
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
fun CommentsHeader(
  postDetails: LobstersPostDetails,
  htmlToMarkdown: (html: String) -> String,
) {
  Surface {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      PostDetails(
        post = postDetails.toDbModel(),
      )
      MaterialRichText { Markdown(htmlToMarkdown(postDetails.description)) }
    }
  }
}

@Composable
fun CommentEntry(
  comment: Comment,
  htmlToMarkdown: (html: String) -> String,
) {
  val indentLevel = comment.indentLevel.toInt() - 1

  Divider(color = Color.Gray.copy(0.4f))
  Row(modifier = Modifier.wrapContentHeight()) {
    CommentTreeColors(indentLevel = indentLevel)
    Column(modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
      SubmitterName(
        text = "Submitted by ${comment.user.username}",
        avatarUrl = "https://lobste.rs/${comment.user.avatarUrl}",
        contentDescription = "Submitted by ${comment.user.username}",
      )
      MaterialRichText(modifier = Modifier.padding(top = 8.dp)) {
        Markdown(htmlToMarkdown(comment.comment))
      }
    }
  }
}

@Composable
private fun CommentTreeColors(
  indentLevel: Int,
  modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    for (level in 1..indentLevel) {
      Box(
        modifier =
          Modifier.padding(start = (12 + ((level - 1) * 10)).dp)
            .fillMaxHeight()
            .width(1.dp)
            .background(CommentTreeColor[level])
      )
    }
  }
}
