package dev.msfjarvis.lobsters.ui.comments

import android.text.Html
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.model.Comment
import dev.msfjarvis.lobsters.ui.posts.SubmitterAvatar

@Composable
fun CommentEntry(
  comment: Comment,
) {
  val indentLevel = comment.indentLevel.toInt() - 1
  val startPadding = ((10 * indentLevel) + 16).dp

  Divider(color = Color.Gray.copy(0.4f))

  Row(modifier = Modifier.padding(start = startPadding, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
    // TODO: Compose currently does not support HTML spanned string
    val text = Html.fromHtml(comment.comment).toString().trim()

    Column {
      Row {
        SubmitterAvatar(
          name = comment.user.username,
          avatarUrl = comment.user.avatarUrl,
        )
        Text(
          text = comment.user.username,
          modifier = Modifier.padding(start = 4.dp),
          style = TextStyle(color = Color.DarkGray, fontWeight = FontWeight.Bold),
        )
      }
      Text(text = text, modifier = Modifier.padding(top = 8.dp))
    }
  }
}
