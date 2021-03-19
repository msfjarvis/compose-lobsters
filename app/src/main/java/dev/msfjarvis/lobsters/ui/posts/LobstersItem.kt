package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.star_zero.compose.flowlayout.FlowLayout
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.data.api.LobstersApi
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.theme.titleColor
import dev.msfjarvis.lobsters.util.IconResource

val TEST_POST = SavedPost(
  shortId = "zqyydb",
  title = "k2k20 hackathon report: Bob Beck on LibreSSL progress",
  url = "https://undeadly.org/cgi?action=article;sid=20200921105847",
  createdAt = "2020-09-21T07:11:14.000-05:00",
  commentsUrl = "https://lobste.rs/s/zqyydb/k2k20_hackathon_report_bob_beck_on",
  submitterName = "Vigdis",
  submitterAvatarUrl = "/avatars/Vigdis-100.png",
  tags = listOf("openbsd", "linux", "containers", "hack the planet", "no thanks"),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LobstersItem(
  post: SavedPost,
  isSaved: Boolean,
  viewPost: () -> Unit,
  viewComments: () -> Unit,
  toggleSave: () -> Unit,
) {
  Surface(
    modifier = Modifier
      .clickable { viewPost.invoke() },
  ) {
    Row(
      modifier = Modifier.padding(start = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Box(
        modifier = Modifier.weight(0.8f),
      ) {
        PostDetails(
          post,
        )
      }
      Box(
        modifier = Modifier.weight(0.1f),
      ) {
        SaveButton(
          isSaved,
          toggleSave,
        )
      }
      Box(
        modifier = Modifier.weight(0.1f),
      ) {
        CommentsButton(
          onClick = viewComments,
        )
      }
    }
  }
}

@Composable
fun PostDetails(
  post: SavedPost,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
  ) {
    Text(
      text = post.title,
      color = titleColor,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .padding(top = 8.dp),
    )
    TagRow(
      tags = post.tags,
      modifier = Modifier
        .padding(top = 4.dp),
    )
    Row(
      verticalAlignment = Alignment.CenterVertically,
    ) {
      CoilImage(
        data = "${LobstersApi.BASE_URL}/${post.submitterAvatarUrl}",
        contentDescription = stringResource(
          R.string.avatar_content_description,
          post.submitterName
        ),
        fadeIn = true,
        requestBuilder = {
          transformations(CircleCropTransformation())
        },
        modifier = Modifier
          .requiredSize(24.dp)
          .padding(top = 4.dp),
      )
      Text(
        text = stringResource(id = R.string.submitted_by, post.submitterName),
        modifier = Modifier
          .padding(start = 4.dp),
      )
    }
  }
}

@Composable
fun SaveButton(
  isSaved: Boolean,
  onSaveButtonClick: () -> Unit,
) {
  IconToggleButton(
    checked = isSaved,
    onCheckedChange = { onSaveButtonClick.invoke() },
    modifier = Modifier
      .requiredSize(24.dp),
  ) {
    Crossfade(targetState = isSaved) { saved ->
      IconResource(
        resourceId = if (saved) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px,
        tint = MaterialTheme.colors.secondary,
        contentDescription = stringResource(if (saved) R.string.remove_from_saved_posts else R.string.add_to_saved_posts),
      )
    }
  }
}

@Composable
fun CommentsButton(
  onClick: () -> Unit,
) {
  IconButton(
    onClick = onClick,
    modifier = Modifier
      .requiredSize(24.dp),
  ) {
    IconResource(
      resourceId = R.drawable.ic_insert_comment_24px,
      tint = MaterialTheme.colors.secondary,
      contentDescription = stringResource(R.string.open_comments),
    )
  }
}

@Composable
fun TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  FlowLayout(
    modifier = Modifier.then(modifier),
    horizontalSpacing = 8.dp,
    verticalSpacing = 8.dp,
  ) {
    tags.forEach { tag ->
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
          isSaved = false,
          viewPost = {},
          viewComments = {},
          toggleSave = {},
        )
      }
    }
  }
}
