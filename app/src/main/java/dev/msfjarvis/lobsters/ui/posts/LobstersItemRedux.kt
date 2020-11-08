package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ConstrainedLayoutReference
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.ConstraintLayoutScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import coil.transform.CircleCropTransformation
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.msfjarvis.lobsters.R
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
fun LobstersItemRedux(
  post: LobstersPost,
  onClick: (LobstersPost) -> Unit = {},
  onLongClick: (LobstersPost) -> Unit = {},
  onSaveButtonClick: (LobstersPost) -> Unit = {},
) {
  Surface(
    modifier = Modifier.fillMaxWidth()
      .clickable(
        onClick = { onClick.invoke(post) },
        onLongClick = { onLongClick.invoke(post) },
      ),
  ) {
    ConstraintLayout(
      modifier = Modifier.padding(start = 4.dp, end = 4.dp),
    ) {
      val (title, tags, avatar, submitter, saveButton) = createRefs()
      Text(
        text = post.title,
        color = titleColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp)
          .constrainAs(title) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
          },
      )
      TagRow(
        tags = post.tags,
        modifier = Modifier.constrainAs(tags) {
          top.linkTo(title.bottom)
        }.padding(vertical = 8.dp),
      )
      CoilImage(
        data = "https://lobste.rs/${post.submitterUser.avatarUrl}",
        fadeIn = true,
        requestBuilder = {
          transformations(CircleCropTransformation())
        },
        modifier = Modifier.width(30.dp).padding(4.dp)
          .constrainAs(avatar) {
            top.linkTo(tags.bottom)
            start.linkTo(parent.start)
          },
      )
      Text(
        text = "submitted by ${post.submitterUser.username}",
        modifier = Modifier.padding(bottom = 4.dp).constrainAs(submitter) {
          top.linkTo(tags.bottom)
          start.linkTo(avatar.end)
        },
      )
      IconResource(
        resourceId = R.drawable.ic_favorite_border_24px,
        modifier = Modifier.padding(8.dp)
          .clickable(
            onClick = { onSaveButtonClick.invoke(post) },
            indication = RippleIndication(),
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

/**
 * This [Composable] function generates a row-like view of tags using [ConstraintLayout]. Since we
 * don't know before hand how many items we will have, we generate refs ahead of time, take the
 * first 4 elements from [tags] and map nullable values out of that list. This lets us manually
 * add [Text] elements based on how many tags we actually have. Since we're constraining manually
 * as opposed to using some reflection hack, the resulting code is a lot of copy and pasting.
 */
@Composable
fun ConstraintLayoutScope.TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  val (tag1ref, tag2ref, tag3ref, tag4ref) = remember { createRefs() }
  val clampedTags = remember(tags) { (0..3).map(tags::elementAtOrNull).toList() }
  clampedTags[0]?.let { tag ->
    Tag(tag, modifier, tag1ref, null)
  }
  clampedTags[1]?.let { tag ->
    Tag(tag, modifier, tag2ref, tag1ref)
  }
  clampedTags[2]?.let { tag ->
    Tag(tag, modifier, tag3ref, tag2ref)
  }
  clampedTags[3]?.let { tag ->
    Tag(tag, modifier, tag4ref, tag3ref)
  }
}

@Composable
fun ConstraintLayoutScope.Tag(
  text: String,
  modifier: Modifier,
  currentRef: ConstrainedLayoutReference,
  previousRef: ConstrainedLayoutReference?,
) {
  Text(
    text = text,
    modifier = Modifier
      .then(modifier)
      .constrainAs(currentRef) {
        start.linkTo(previousRef?.end ?: parent.start)
      }
      .background(Color(0xFFFFFCD7), RoundedCornerShape(8.dp))
      .padding(vertical = 2.dp, horizontal = 6.dp),
    color = Color.DarkGray,
  )
}

@Composable
@Preview
fun Preview() {
  LobstersTheme {
    LazyColumnFor(items = listOf(TEST_POST)) { item ->
      LobstersItemRedux(
        post = item,
      )
    }
  }
}
