package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.model.Submitter
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.theme.titleColor
import dev.msfjarvis.lobsters.util.IconResource

val TEST_POST = LobstersPost(
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
    emptyList(),
  ),
  listOf("openbsd", "linux", "containers", "hack the planet", "no thanks"),
)

@Composable
fun LobstersItem(
  post: LobstersPost,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  onSaveButtonClick: () -> Unit,
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(
        onClick = onClick,
        onLongClick = onLongClick,
      ),
  ) {
    ConstraintLayout(
      modifier = Modifier.padding(start = 12.dp, end = 12.dp),
    ) {
      val (title, tags, avatar, submitter, saveButton) = createRefs()
      Text(
        text = post.title,
        color = titleColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .padding(top = 4.dp)
          .constrainAs(title) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
          },
      )
      TagRow(
        tags = post.tags,
        modifier = Modifier
          .constrainAs(tags) {
            top.linkTo(title.bottom)
          }
          .padding(vertical = 8.dp),
      )
      CoilImage(
        data = "${LobstersApi.BASE_URL}/${post.submitterUser.avatarUrl}",
        fadeIn = true,
        requestBuilder = {
          transformations(CircleCropTransformation())
        },
        modifier = Modifier
          .width(30.dp)
          .padding(4.dp)
          .constrainAs(avatar) {
            top.linkTo(tags.bottom)
            start.linkTo(parent.start)
          },
      )
      Text(
        text = "submitted by ${post.submitterUser.username}",
        modifier = Modifier
          .padding(4.dp)
          .constrainAs(submitter) {
            top.linkTo(avatar.top)
            bottom.linkTo(avatar.bottom)
            start.linkTo(avatar.end)
          },
      )
      IconResource(
        resourceId = R.drawable.ic_favorite_border_24px,
        modifier = Modifier
          .padding(8.dp)
          .clickable(
            onClick = onSaveButtonClick,
            indication = rememberRipple(),
          )
          .constrainAs(saveButton) {
            end.linkTo(parent.end)
            centerVerticallyTo(parent)
          },
        tint = Color(0xFFD97373),
      )
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
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    tags.take(3).forEach { tag ->
      Text(
        text = tag,
        modifier = Modifier
          .background(Color(0xFFFFFCD7), RoundedCornerShape(8.dp))
          .padding(vertical = 2.dp, horizontal = 6.dp),
        color = Color.DarkGray,
      )
    }
  }
}

@Composable
@Preview
fun Preview() {
  LobstersTheme {
    LazyColumn {
      items(listOf(TEST_POST, TEST_POST, TEST_POST, TEST_POST, TEST_POST)) { item ->
        LobstersItem(
          post = item,
          onClick = {},
          onLongClick = {},
          onSaveButtonClick = {},
        )
      }
    }
  }
}
