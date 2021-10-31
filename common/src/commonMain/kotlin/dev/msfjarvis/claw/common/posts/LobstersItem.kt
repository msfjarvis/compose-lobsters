package dev.msfjarvis.claw.common.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.msfjarvis.claw.common.res.commentIcon
import dev.msfjarvis.claw.common.res.heartBorderIcon
import dev.msfjarvis.claw.common.res.heartIcon
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.database.local.SavedPost

@Composable
@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalFoundationApi::class,
)
fun LobstersCard(
  post: SavedPost,
  isSaved: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  var localSavedState by remember(isSaved) { mutableStateOf(isSaved) }
  Box(modifier = modifier.clickable { postActions.viewPost(post.url, post.commentsUrl) }) {
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
          isSaved = localSavedState,
          modifier =
            Modifier.clickable {
              localSavedState = localSavedState.not()
              postActions.toggleSave(post)
            },
        )
        Spacer(
          modifier = Modifier.width(8.dp),
        )
        CommentsButton(
          modifier =
            Modifier.combinedClickable(
              onClick = { postActions.viewComments(post.shortId) },
              onLongClick = { postActions.viewCommentsPage(post.commentsUrl) },
            ),
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
    contentDescription = "User avatar for ${post.submitterName}",
  )
}

@Composable
fun PostTitle(
  title: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = title,
    fontWeight = FontWeight.Bold,
    modifier = modifier,
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
    modifier = modifier,
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
  modifier: Modifier = Modifier,
) {
  NetworkImage(
    url = avatarUrl,
    contentDescription = contentDescription,
    modifier = modifier.requiredSize(24.dp).clip(CircleShape),
  )
}

@Composable
fun SubmitterNameText(
  text: String,
  modifier: Modifier,
) {
  Text(
    text = text,
    modifier = modifier,
  )
}

@Composable
fun SaveButton(
  isSaved: Boolean,
  modifier: Modifier = Modifier,
) {
  Crossfade(targetState = isSaved) { saved ->
    Icon(
      painter = if (saved) heartIcon else heartBorderIcon,
      tint = MaterialTheme.colorScheme.secondary,
      contentDescription = if (saved) "Remove from saved posts" else "Add to saved posts",
      modifier = modifier,
    )
  }
}

@Composable
fun CommentsButton(
  modifier: Modifier = Modifier,
) {
  Icon(
    painter = commentIcon,
    tint = MaterialTheme.colorScheme.secondary,
    contentDescription = "Open comments",
    modifier = modifier,
  )
}

@Composable
fun TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier,
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
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.75f),
                RoundedCornerShape(8.dp)
              )
              .padding(vertical = 2.dp, horizontal = 6.dp),
          color = MaterialTheme.colorScheme.onSecondary,
        )
      }
    }
  }
}
