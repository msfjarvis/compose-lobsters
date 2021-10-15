package dev.msfjarvis.claw.common.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.msfjarvis.claw.common.res.commentIcon
import dev.msfjarvis.claw.common.res.heartBorderIcon
import dev.msfjarvis.claw.common.res.heartIcon
import dev.msfjarvis.claw.common.theme.titleColor
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.database.local.SavedPost

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun LobstersCard(
  post: SavedPost,
  isSaved: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = Modifier.background(MaterialTheme.colors.primarySurface).then(modifier),
    onClick = { postActions.viewPost(post.url, post.commentsUrl) },
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      PostDetails(
        post = post,
      )
      Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
      ) {
        SaveButton(
          isSaved = isSaved,
          onClick = { postActions.toggleSave(post) },
        )
        Spacer(
          modifier = Modifier.width(8.dp),
        )
        CommentsButton(
          onClick = { postActions.viewComments(post.shortId) },
        )
      }
    }
  }
}

@Composable
fun PostDetails(
  post: SavedPost,
) {
  PostTitle(
    title = post.title,
  )
  TagRow(
    tags = post.tags,
  )
  SubmitterName(
    text = "Submitted by ${post.submitterName}",
    avatarUrl = "https://lobste.rs/${post.submitterAvatarUrl}",
    contentDescription = "Submitted by ${post.submitterName}",
  )
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
      modifier = Modifier.padding(start = 4.dp),
    )
  }
}

@Composable
fun SubmitterAvatar(
  avatarUrl: String,
  contentDescription: String,
) {
  NetworkImage(
    url = avatarUrl,
    contentDescription = contentDescription,
    modifier = Modifier.requiredSize(24.dp).clip(CircleShape),
  )
}

@Composable
fun SubmitterNameText(
  text: String,
  modifier: Modifier,
) {
  Text(
    text = text,
    modifier = Modifier.then(modifier),
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
      Icon(
        painter = if (saved) heartIcon else heartBorderIcon,
        tint = MaterialTheme.colors.secondary,
        contentDescription = if (saved) "Remove from saved posts" else "Add to saved posts",
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
    Icon(
      painter = commentIcon,
      tint = MaterialTheme.colors.secondary,
      contentDescription = "Open comments",
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
