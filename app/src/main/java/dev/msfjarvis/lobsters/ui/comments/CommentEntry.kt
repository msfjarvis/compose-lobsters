package dev.msfjarvis.lobsters.ui.comments

import android.text.Html
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.model.Comment

@Composable
fun CommentEntry(
  comment: Comment,
) {
  Row(modifier = Modifier.padding(start = (10 * (comment.indentLevel.toFloat() - 1)).dp)) {
    Text(
      text = buildAnnotatedString { @Suppress("DEPRECATION") Html.fromHtml(comment.comment) },
    )
  }
}
