package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.flowlayout.FlowRow
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.theme.titleColor
import dev.msfjarvis.lobsters.util.IconResource
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.get

val TEST_POST =
  SavedPost(
    shortId = "zqyydb",
    title = "k2k20 hackathon report: Bob Beck on LibreSSL progress",
    url = "https://undeadly.org/cgi?action=article;sid=20200921105847",
    createdAt = "2020-09-21T07:11:14.000-05:00",
    commentsUrl = "https://lobste.rs/s/zqyydb/k2k20_hackathon_report_bob_beck_on",
    submitterName = "Vigdis",
    submitterAvatarUrl = "/404.html",
    tags = listOf("openbsd", "linux", "containers", "hack the planet", "no thanks"),
  )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LobstersItem(
  post: SavedPost,
  isSaved: Boolean,
  viewPost: () -> Unit,
  viewComments: (String) -> Unit,
  toggleSave: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    modifier = Modifier.clickable { viewPost.invoke() }.then(modifier),
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
      PostTitle(
        title = post.title,
        modifier = Modifier.padding(bottom = 4.dp),
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TagRow(
          tags = post.tags,
          modifier = Modifier.weight(0.65f),
        )
        SaveButton(
          isSaved = isSaved,
          onClick = toggleSave,
        )
        Spacer(
          modifier = Modifier.width(8.dp),
        )
        CommentsButton(
          onClick = { viewComments(post.shortId) },
        )
      }
      SubmitterName(
        text = Strings.SubmittedBy.get(post.submitterName),
        avatarUrl = post.submitterAvatarUrl,
        contentDescription = Strings.AvatarContentDescription.get(post.submitterName),
      )
    }
  }
}

@Composable
fun PostTitle(
  title: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = title,
    color = titleColor,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.then(modifier),
  )
}

@Composable
fun SubmitterName(
  text: String,
  avatarUrl: String,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = Modifier.then(modifier),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    SubmitterAvatar(
      avatarUrl = avatarUrl,
      contentDescription = contentDescription,
    )
    SubmitterNameText(
      text = text,
    )
  }
}

@Composable
fun SubmitterAvatar(
  avatarUrl: String,
  contentDescription: String,
) {
  Image(
    painter =
      rememberCoilPainter(
        request = "${LobstersApi.BASE_URL}/$avatarUrl",
        requestBuilder = { transformations(CircleCropTransformation()) },
        fadeIn = true,
      ),
    contentDescription = contentDescription,
    modifier = Modifier.requiredSize(24.dp),
  )
}

@Composable
fun SubmitterNameText(
  text: String,
) {
  Text(
    text = text,
    modifier = Modifier.padding(start = 4.dp),
  )
}

@Composable
fun SaveButton(
  isSaved: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconToggleButton(
    checked = isSaved,
    onCheckedChange = { onClick.invoke() },
    modifier = Modifier.requiredSize(32.dp).then(modifier),
  ) {
    Crossfade(targetState = isSaved) { saved ->
      IconResource(
        resourceId = if (saved) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px,
        tint = MaterialTheme.colors.secondary,
        contentDescription =
          if (saved) Strings.RemoveFromSavedPosts.get() else Strings.AddToSavedPosts.get(),
      )
    }
  }
}

@Composable
fun CommentsButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = onClick,
    modifier = Modifier.requiredSize(32.dp).then(modifier),
  ) {
    IconResource(
      resourceId = R.drawable.ic_insert_comment_24px,
      tint = MaterialTheme.colors.secondary,
      contentDescription = Strings.OpenComments.get(),
    )
  }
}

@Composable
fun TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = Modifier.then(modifier),
  ) {
    FlowRow(
      mainAxisSpacing = 8.dp,
      crossAxisSpacing = 8.dp,
    ) {
      tags.forEach { tag ->
        Text(
          text = tag,
          modifier =
            Modifier.background(
                MaterialTheme.colors.secondary.copy(alpha = 0.75f),
                RoundedCornerShape(8.dp)
              )
              .padding(vertical = 2.dp, horizontal = 6.dp),
          color = MaterialTheme.colors.onSecondary,
        )
      }
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
          isSaved = false,
          viewPost = {},
          viewComments = {},
          toggleSave = {},
        )
      }
    }
  }
}
