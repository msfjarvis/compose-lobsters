package dev.msfjarvis.claw.common.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.msfjarvis.claw.common.res.commentIcon
import dev.msfjarvis.claw.common.res.heartBorderIcon
import dev.msfjarvis.claw.common.res.heartIcon
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.database.local.SavedPost

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LobstersCard(
  post: SavedPost,
  isSaved: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  var localSavedState by remember(post, isSaved) { mutableStateOf(isSaved) }
  Box(modifier = modifier.clickable { postActions.viewPost(post.url, post.commentsUrl) }) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PostDetails(
        modifier = Modifier.weight(1f),
        post = post,
      )
      Column(
        modifier = Modifier.weight(0.15f).fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        SaveButton(
          isSaved = localSavedState,
          modifier =
            Modifier.clickable(
              role = Role.Button,
              indication = rememberRipple(bounded = false, radius = 24.dp),
              interactionSource = remember { MutableInteractionSource() },
            ) {
              localSavedState = !localSavedState
              postActions.toggleSave(post)
            },
        )
        Divider()
        CommentsButton(
          modifier =
            Modifier.combinedClickable(
              role = Role.Button,
              indication = rememberRipple(bounded = false, radius = 24.dp),
              interactionSource = remember { MutableInteractionSource() },
              onClick = { postActions.viewComments(post.shortId) },
              onLongClick = { postActions.viewCommentsPage(post.commentsUrl) },
            ),
        )
      }
    }
  }
}

@Composable
fun PostDetails(post: SavedPost, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PostTitle(title = post.title)
    TagRow(tags = post.tags)
    Spacer(Modifier.height(4.dp))
    Submitter(
      text = "Submitted by ${post.submitterName}",
      avatarUrl = "https://lobste.rs/${post.submitterAvatarUrl}",
      contentDescription = "User avatar for ${post.submitterName}",
    )
  }
}

@Composable
fun PostTitle(
  title: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = title,
    modifier = modifier,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.Bold
  )
}

@Composable
fun Submitter(
  text: String,
  avatarUrl: String,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    NetworkImage(
      url = avatarUrl,
      contentDescription = contentDescription,
      modifier = modifier.requiredSize(24.dp).clip(CircleShape),
    )

    Text(text = text, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
  }
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
      modifier = modifier.padding(12.dp),
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
    modifier = modifier.padding(12.dp),
  )
}

@Composable
fun TagRow(
  tags: List<String>,
  modifier: Modifier = Modifier,
) {
  FlowRow(
    modifier = modifier,
    mainAxisSpacing = 8.dp,
    crossAxisSpacing = 8.dp,
  ) { tags.forEach { tag -> TagText(tag) } }
}

@Composable
fun TagText(
  tag: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = tag,
    modifier =
      Modifier.background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(50))
        .padding(vertical = 4.dp, horizontal = 12.dp)
        .then(modifier),
    color = MaterialTheme.colorScheme.onTertiaryContainer,
    style = MaterialTheme.typography.labelLarge
  )
}
