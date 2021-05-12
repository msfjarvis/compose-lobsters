package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import io.kamel.image.KamelImage
import io.kamel.image.lazyImageResource
import java.net.URI

@Composable
fun LobstersItem(
  post: SavedPost,
) {
  val urlLauncher = LocalUrlLauncher.current
  Surface(
    modifier =
      Modifier.fillMaxWidth().clickable { urlLauncher.launch(post.url) }.wrapContentHeight(),
  ) {
    Row(
      modifier = Modifier.padding(start = 12.dp, end = 24.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text(
          text = post.title,
          color = titleColor,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(top = 4.dp),
        )
        TagRow(
          tags = post.tags,
          modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
        )
        Row {
          KamelImage(
            resource = lazyImageResource(data = URI(post.submitterAvatarUrl)),
            contentDescription = "${post.submitterName}'s avatar",
            modifier = Modifier.requiredWidth(30.dp).padding(4.dp),
          )
          Text(
            text = "Submitted by ${post.submitterName}",
            modifier = Modifier.padding(4.dp),
          )
        }
      }
    }
  }
}

@Composable
fun TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = Modifier.then(modifier),
  ) {
    tags.forEach { tag ->
      Text(
        text = tag,
        modifier =
          Modifier.background(Color(0xFFFFFCD7), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp),
        color = Color.DarkGray,
      )
    }
  }
}
