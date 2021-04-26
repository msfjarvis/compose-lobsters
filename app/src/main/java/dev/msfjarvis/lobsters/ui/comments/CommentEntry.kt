package dev.msfjarvis.lobsters.ui.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.model.Comment
import dev.msfjarvis.lobsters.ui.posts.SubmitterName
import dev.msfjarvis.lobsters.util.toNormalizedHtml
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.get

@Composable
fun CommentEntry(
  comment: Comment,
) {
  val indentLevel = comment.indentLevel.toInt() - 1

  Row(modifier = Modifier.height(IntrinsicSize.Min)) {
    CommentTreeColors(indentLevel = indentLevel)
    Column(modifier = Modifier.padding(bottom = 4.dp, end = 8.dp, start = 12.dp, top = 4.dp)) {
      CompositionLocalProvider(LocalTextStyle provides TextStyle(fontWeight = FontWeight.Bold)) {
        SubmitterName(
          text = comment.user.username,
          avatarUrl = comment.user.avatarUrl,
          contentDescription = Strings.AvatarContentDescription.get(comment.user.username),
        )
      }
      Text(text = comment.comment.toNormalizedHtml(), modifier = Modifier.padding(top = 8.dp))
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
        modifier = Modifier
          .padding(start = (level * 12).dp)
          .fillMaxHeight()
          .width(1.dp)
          .background(CommentTreeColor[level])
      )
    }
  }
}
