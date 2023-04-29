/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.res.ClawIcons
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun LobstersCard(
  post: SavedPost,
  isSaved: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  var localSavedState by remember(post, isSaved) { mutableStateOf(isSaved) }
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .clickable { postActions.viewPost(post.url, post.commentsUrl) }
        .padding(start = 16.dp, top = 16.dp, end = 4.dp, bottom = 16.dp),
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PostDetails(
        modifier = Modifier.weight(1f),
        post = post,
      )
      Column(
        modifier = Modifier.wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        SaveButton(
          isSaved = localSavedState,
          modifier =
            Modifier.clickable(role = Role.Button) {
              localSavedState = !localSavedState
              postActions.toggleSave(post)
            },
        )
        Divider(modifier = Modifier.width(48.dp))
        CommentsButton(
          commentCount = post.commentCount,
          modifier =
            Modifier.combinedClickable(
              role = Role.Button,
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
    TagRow(tags = post.tags.toImmutableList())
    Spacer(Modifier.height(4.dp))
    Submitter(
      text = AnnotatedString("Submitted by ${post.submitterName}"),
      avatarUrl = "https://lobste.rs/${post.submitterAvatarUrl}",
      contentDescription = "User avatar for ${post.submitterName}",
    )
  }
}

@Composable
internal fun PostTitle(
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
internal fun Submitter(
  text: AnnotatedString,
  avatarUrl: String,
  contentDescription: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    NetworkImage(
      url = avatarUrl,
      placeholder = ClawIcons.Account,
      contentDescription = contentDescription,
      modifier = Modifier.requiredSize(24.dp).clip(CircleShape),
    )

    Text(text = text, style = MaterialTheme.typography.bodyMedium)
  }
}

@Composable
private fun SaveButton(
  isSaved: Boolean,
  modifier: Modifier = Modifier,
) {
  Crossfade(targetState = isSaved, label = "save-button") { saved ->
    Box(modifier = modifier.padding(12.dp)) {
      Icon(
        painter = if (saved) ClawIcons.Heart else ClawIcons.HeartBorder,
        tint = MaterialTheme.colorScheme.secondary,
        contentDescription = if (saved) "Remove from saved posts" else "Add to saved posts",
        modifier = Modifier.align(Alignment.Center)
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CommentsButton(
  commentCount: Int?,
  modifier: Modifier = Modifier,
) {
  BadgedBox(
    modifier = modifier.padding(12.dp),
    badge = {
      if (commentCount != null) {
        Badge(
          modifier = Modifier.absoluteOffset(x = -(8.dp)),
          containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
          Text(
            text = commentCount.toString(),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }
    },
  ) {
    Icon(
      painter = ClawIcons.Comment,
      tint = MaterialTheme.colorScheme.secondary,
      contentDescription = "Open comments",
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun TagRow(
  tags: ImmutableList<String>,
  modifier: Modifier = Modifier,
) {
  FlowRow(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    tags.forEach { tag ->
      TagText(tag = tag, modifier = Modifier.padding(end = 8.dp, top = 4.dp, bottom = 4.dp))
    }
  }
}

@Composable
private fun TagText(
  tag: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = tag,
    modifier =
      modifier
        .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
        .padding(vertical = 4.dp, horizontal = 12.dp),
    color = MaterialTheme.colorScheme.onTertiaryContainer,
    style = MaterialTheme.typography.labelLarge,
  )
}

@ThemePreviews
@Composable
fun LobstersCardPreview() {
  LobstersTheme {
    LobstersCard(
      post =
        SavedPost(
          shortId = "ooga",
          title = "Simple Anomaly Detection Using Plain SQL",
          url = "https://hakibenita.com/sql-anomaly-detection",
          createdAt = "2020-09-21T08:04:24.000-05:00",
          commentCount = 1,
          commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
          submitterName = "Haki",
          submitterAvatarUrl = "/avatars/Haki-100.png",
          tags = listOf("databases", "apis"),
          description = "",
        ),
      isSaved = true,
      postActions =
        object : PostActions {
          override fun viewPost(postUrl: String, commentsUrl: String) {}

          override fun viewComments(postId: String) {}

          override fun viewCommentsPage(commentsUrl: String) {}

          override fun toggleSave(post: SavedPost) {}

          override suspend fun getComments(postId: String): LobstersPostDetails {
            return LobstersPostDetails(
              shortId = "ooga",
              title = "Simple Anomaly Detection Using Plain SQL",
              url = "https://hakibenita.com/sql-anomaly-detection",
              createdAt = "2020-09-21T08:04:24.000-05:00",
              commentCount = 1,
              commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
              tags = listOf("databases", "apis"),
              description = "",
              submitter = User("Haki", "", "", "", ""),
              comments = emptyList(),
            )
          }

          override suspend fun getLinkMetadata(url: String): LinkMetadata {
            return LinkMetadata("", "", "")
          }
        },
    )
  }
}
