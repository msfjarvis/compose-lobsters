@file:Suppress("FunctionName")

package dev.msfjarvis.claw.common.posts

import androidx.compose.animation.Crossfade
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import dev.msfjarvis.lobsters.data.local.SavedPost
import io.kamel.image.KamelImage
import io.kamel.image.lazyImageResource

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
        text = "Submitted by %s".format(post.submitterName),
        avatarUrl = post.submitterAvatarUrl,
        contentDescription = "Submitted by %s".format(post.submitterName),
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
    color = Color.Black,
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
  KamelImage(
    resource = lazyImageResource(avatarUrl),
    contentDescription = contentDescription,
    modifier = Modifier.requiredSize(24.dp),
    crossfade = true,
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
      /*IconResource(
        resourceId = if (saved) R.drawable.ic_favorite_24px else R.drawable.ic_favorite_border_24px,
        tint = MaterialTheme.colors.secondary,
        contentDescription =
        if (saved) Strings.RemoveFromSavedPosts.get() else Strings.AddToSavedPosts.get(),
      )*/
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
    /*IconResource(
      resourceId = R.drawable.ic_insert_comment_24px,
      tint = MaterialTheme.colors.secondary,
      contentDescription = Strings.OpenComments.get(),
    )*/
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
