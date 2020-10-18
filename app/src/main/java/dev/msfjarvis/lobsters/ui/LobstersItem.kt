package dev.msfjarvis.lobsters.ui

import androidx.compose.animation.animate
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import coil.transform.CircleCropTransformation
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.msfjarvis.lobsters.di.ApiModule
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.Submitter

@Composable
fun LazyItemScope.LobstersItem(
  post: LobstersPost,
  modifier: Modifier = Modifier,
  linkOpenAction: (LobstersPost) -> Unit,
  commentOpenAction: (LobstersPost) -> Unit,
  saveAction: (LobstersPost) -> Unit,
) {
  val liked = remember { mutableStateOf(false) }
  val titleColor = if (post.isLiked || liked.value) savedTitleColor else titleColor

  Column(
    modifier = modifier
      .fillParentMaxWidth()
      .clickable(
        onClick = { linkOpenAction.invoke(post) },
        onLongClick = { commentOpenAction.invoke(post) },
        onDoubleClick = {
          post.isLiked = true
          liked.value = true
          saveAction.invoke(post)
        },
      ),
  ) {
    Text(
      text = post.title,
      color = titleColor,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(top = 4.dp)
    )
    Row(
      modifier = Modifier.padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      post.tags.take(4).forEach { tag ->
        Text(
          text = tag,
          modifier = Modifier
            .background(Color(0xFFFFFCD7), RoundedCornerShape(8.dp))
            .padding(vertical = 2.dp, horizontal = 6.dp),
          color = Color.DarkGray,
        )
      }
    }
    Row(
      modifier = Modifier.wrapContentHeight(),
    ) {
      CoilImage(
        data = "${ApiModule.LOBSTERS_URL}/${post.submitterUser.avatarUrl}",
        fadeIn = true,
        requestBuilder = {
          transformations(CircleCropTransformation())
        },
        modifier = Modifier.width(30.dp).padding(4.dp).align(Alignment.CenterVertically),
      )
      Text(
        text = "submitted by ${post.submitterUser.username}",
        modifier = Modifier.padding(bottom = 4.dp).align(Alignment.CenterVertically),
      )
    }
  }
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
    listOf("openbsd"),
  )
  LobstersTheme {
    LazyColumnFor(items = listOf(post)) { item ->
      LobstersItem(
        post = item,
        linkOpenAction = {},
        commentOpenAction = {},
        saveAction = {})
    }
  }
}
