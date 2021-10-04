package dev.msfjarvis.claw.common.comments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.posts.PostDetails
import dev.msfjarvis.claw.common.posts.SubmitterName
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
fun CommentsHeader(
  postDetails: LobstersPostDetails,
) {
  Surface {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      PostDetails(
        post = postDetails.toDbModel(),
      )
    }
  }
}

@Composable
fun CommentEntry(
  comment: Comment,
  parseHtml: (String) -> String,
) {
  val indentLevel = comment.indentLevel.toInt() - 1
  val startPadding = ((10 * indentLevel) + 16).dp

  Divider(color = Color.Gray.copy(0.4f))

  Row(modifier = Modifier.padding(start = startPadding, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
    val text = parseHtml(comment.comment)
    Column {
      SubmitterName(
        text = "Submitted by ${comment.user.username}",
        avatarUrl = "https://lobste.rs/${comment.user.avatarUrl}",
        contentDescription = "Submitted by ${comment.user.username}",
      )
      Text(text = text, modifier = Modifier.padding(top = 8.dp))
    }
  }
}
