package dev.msfjarvis.lobsters.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.Submitter

@Composable
fun LazyItemScope.LobstersItem(
  post: LobstersPost,
  modifier: Modifier = Modifier,
  onClick: (LobstersPost) -> Unit,
) {
  ListItem(
    modifier = modifier.padding(horizontal = 8.dp)
      .fillParentMaxWidth()
      .clickable(onClick = { onClick.invoke(post) }),
    text = {
      Column {
        Text(
          text = post.title,
          color = Color(0xFF7395D9),
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(top = 4.dp)
        )
        Row(
          modifier = Modifier.padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          post.tags.forEach { tag ->
            Text(
              text = tag,
              modifier = Modifier
                .border(BorderStroke(1.dp, Color.Gray))
                .background(Color(0xFFFFFCD7), RoundedCornerShape(4.dp))
                .padding(vertical = 2.dp, horizontal = 4.dp),
              color = Color.DarkGray,
            )
          }
        }
        Text(
          text = "submitted by ${post.submitterUser.username}",
          modifier = Modifier.padding(bottom = 4.dp),
        )
      }
    }
  )
}

@Composable
@Preview
fun PreviewLobstersItem() {
  val post = LobstersPost(
    "zqyydb",
    "https://lobste.rs/s/zqyydb",
    "2020-09-21T07:11:14.000-05:00",
    "k2k20 hackathon report: Bob Beck on LibreSSL progress",
    "https://undeadly.org/cgi?action=article;sid=20200921105847",
    4,
    0,
    0,
    "",
    "https://lobste.rs/s/zqyydb/k2k20_hackathon_report_bob_beck_on",
    Submitter(
      "Vigdis",
      "2017-02-27T21:08:14.000-06:00",
      false,
      "Alleycat for the fun, sys/net admin for a living and OpenBSD contributions for the pleasure. (Not so) French dude in Montreal\r\n\r\nhttps://chown.me",
      false,
      76,
      "/avatars/Vigdis-100.png",
      "sevan",
      null,
      null,
      null,
    ),
    listOf("openbsd")
  )
  LobstersTheme {
    LazyColumnFor(items = listOf(post)) { item ->
      LobstersItem(post = item, onClick = {})
    }
  }
}