/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun LobstersCard(post: UIPost, postActions: PostActions, modifier: Modifier = Modifier) {
  val readState by remember(post.shortId) { derivedStateOf { postActions.isPostRead(post) } }
  val savedState by remember(post.shortId) { derivedStateOf { postActions.isPostSaved(post) } }
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .clickable { postActions.viewPost(post.shortId, post.url, post.commentsUrl) }
        .background(MaterialTheme.colorScheme.background)
        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PostDetails(
        post = post,
        isRead = { readState },
        singleLineTitle = true,
        modifier = Modifier.weight(1f),
      )
      Column(
        modifier = Modifier.wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        SaveButton(isSaved = { savedState }, onClick = { postActions.toggleSave(post) })
        HorizontalDivider(modifier = Modifier.width(48.dp))
        CommentsButton(
          commentCount = post.commentCount,
          modifier =
            Modifier.clickable(
              role = Role.Button,
              onClick = { postActions.viewComments(post.shortId) },
            ),
        )
      }
    }
  }
}

@Composable
fun PostDetails(
  post: UIPost,
  isRead: () -> Boolean,
  singleLineTitle: Boolean,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PostTitle(title = post.title, isRead = isRead(), singleLineTitle = singleLineTitle)
    TagRow(tags = post.tags.toImmutableList())
    Spacer(Modifier.height(4.dp))
    Submitter(
      text =
        AnnotatedString(
          "${if (post.userIsAuthor) "Authored" else "Submitted"} by ${post.submitter}"
        ),
      avatarUrl = "https://lobste.rs/avatars/${post.submitter}-100.png",
      contentDescription = "User avatar for ${post.submitter}",
    )
  }
}

@Composable
internal fun PostTitle(
  title: String,
  isRead: Boolean,
  singleLineTitle: Boolean,
  modifier: Modifier = Modifier,
) {
  Text(
    text = title,
    modifier = modifier,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground,
    maxLines = if (singleLineTitle) 1 else Int.MAX_VALUE,
    overflow = TextOverflow.Ellipsis,
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
      placeholder = Icons.Filled.AccountCircle,
      contentDescription = contentDescription,
      modifier = Modifier.requiredSize(24.dp).clip(CircleShape),
      placeholderTintColor = MaterialTheme.colorScheme.onBackground,
    )

    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onBackground,
    )
  }
}

@Composable
private fun SaveButton(isSaved: () -> Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  var localSavedState by remember { mutableStateOf(isSaved()) }
  Crossfade(targetState = localSavedState, label = "save-button") { saved ->
    Box(modifier = modifier.minimumInteractiveComponentSize()) {
      Icon(
        imageVector = if (saved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        tint = MaterialTheme.colorScheme.secondary,
        contentDescription = if (saved) "Remove from saved posts" else "Add to saved posts",
        modifier =
          Modifier.align(Alignment.Center).testTag("save_button").clickable(role = Role.Button) {
            onClick()
            localSavedState = !localSavedState
          },
      )
    }
  }
}

@Composable
private fun CommentsButton(commentCount: Int, modifier: Modifier = Modifier) {
  val offset = run {
    var count = commentCount
    var digits = 1
    while (count > 10) {
      count /= 10
      digits += 1
    }
    if (digits < 3) 0 else digits * -2
  }
  BadgedBox(
    modifier = modifier.minimumInteractiveComponentSize(),
    badge = {
      Badge(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.absoluteOffset(x = offset.dp, y = (-8).dp),
      ) {
        Text(
          text = commentCount.toString(),
          color = MaterialTheme.colorScheme.onTertiaryContainer,
          style = MaterialTheme.typography.labelMedium,
        )
      }
    },
  ) {
    Icon(
      imageVector = Icons.AutoMirrored.Filled.Comment,
      tint = MaterialTheme.colorScheme.secondary,
      contentDescription = "Open comments",
      modifier = Modifier.testTag("comments_button"),
    )
  }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun TagRow(tags: ImmutableList<String>, modifier: Modifier = Modifier) {
  FlowRow(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    tags.forEach { tag -> TagText(tag = tag) }
  }
}

@Composable
private fun TagText(tag: String, modifier: Modifier = Modifier) {
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

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
val TEST_POST_ACTIONS =
  object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {}

    override fun viewComments(postId: String) {}

    override fun viewCommentsPage(post: UIPost) {}

    override fun toggleSave(post: UIPost) {}

    override fun share(post: UIPost) {}

    override fun isPostRead(post: UIPost): Boolean {
      return true
    }

    override fun isPostSaved(post: UIPost): Boolean {
      return true
    }

    override suspend fun getComments(postId: String): UIPost {
      return UIPost(
        shortId = "ooga",
        title = "Simple Anomaly Detection Using Plain SQL",
        url = "https://hakibenita.com/sql-anomaly-detection",
        createdAt = "2020-09-21T08:04:24.000-05:00",
        commentCount = 1,
        commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
        tags = listOf("databases", "apis"),
        description = "",
        submitter = "Haki",
        comments = emptyList(),
      )
    }

    override suspend fun getLinkMetadata(url: String): LinkMetadata {
      return LinkMetadata("", "")
    }
  }

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
val TEST_POST =
  UIPost(
    shortId = "ooga",
    title = "Simple Anomaly Detection Using Plain SQL",
    url = "https://hakibenita.com/sql-anomaly-detection",
    createdAt = "2020-09-21T08:04:24.000-05:00",
    commentCount = 1,
    commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
    submitter = "Haki",
    tags = listOf("databases", "apis"),
    description = "",
  )

@ThemePreviews
@Composable
private fun LobstersCardPreview() {
  LobstersTheme { LobstersCard(post = TEST_POST, postActions = TEST_POST_ACTIONS) }
}
