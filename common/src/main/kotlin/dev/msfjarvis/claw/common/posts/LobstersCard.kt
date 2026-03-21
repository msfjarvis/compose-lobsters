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
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.R
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.NetworkImage
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun LobstersCard(
  post: UIPost,
  isSaved: Boolean,
  isRead: Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier
        .clickable { postActions.viewPost(post.shortId, post.url, post.commentsUrl) }
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 12.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    PostDetails(post = post, isRead = { isRead }, modifier = Modifier.weight(1f))
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      SaveButton(isSaved = isSaved, onClick = { postActions.toggleSave(post) })
      CommentsButton(
        commentCount = post.commentCount,
        onClick = { postActions.viewComments(post.shortId) },
      )
    }
  }
}

@Composable
fun PostDetails(post: UIPost, isRead: () -> Boolean, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    PostTitle(title = post.title, isRead = isRead(), maxLines = 2)
    TagRow(tags = post.tags.toImmutableList())
    Spacer(Modifier.height(4.dp))
    Submitter(
      text =
        AnnotatedString(
          "${if (post.userIsAuthor) "Authored" else "Submitted"} by ${post.submitter}"
        ),
      avatarUrl = "https://lobste.rs/avatars/${post.submitter}-100.png",
      contentDescription = stringResource(R.string.user_avatar_for, post.submitter),
    )
  }
}

@Composable
internal fun PostTitle(
  title: String,
  isRead: Boolean,
  modifier: Modifier = Modifier,
  maxLines: Int = Int.MAX_VALUE,
) {
  Text(
    text = title,
    modifier = modifier,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
    color = MaterialTheme.colorScheme.onBackground,
    maxLines = maxLines,
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
private fun SaveButton(isSaved: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Crossfade(targetState = isSaved, label = "save-button") { saved ->
    Button(
      modifier = modifier,
      onClick = onClick,
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      colors =
        ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
      Icon(
        imageVector = if (saved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = if (saved) "Remove from saved posts" else "Add to saved posts",
        modifier = Modifier.testTag("save_button"),
      )
    }
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CommentsButton(commentCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Button(
    modifier = modifier,
    onClick = onClick,
    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 4.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      ),
  ) {
    Box(modifier = Modifier.size(width = 42.dp, height = 40.dp)) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.Comment,
        contentDescription = stringResource(R.string.open_comments),
        modifier = Modifier.align(Alignment.Center).testTag("comments_button"),
      )
      Box(
        modifier =
          Modifier.align(Alignment.TopEnd)
            .requiredSizeIn(16.dp, 16.dp)
            .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape)
            .testTag("comment_badge"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = commentCount.toString(),
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onTertiaryContainer,
          modifier = Modifier.testTag("comment_count").padding(2.dp),
        )
      }
    }
  }
}

@Composable
@OptIn(ExperimentalFlexBoxApi::class)
internal fun TagRow(tags: ImmutableList<String>, modifier: Modifier = Modifier) {
  FlexBox(
    modifier = modifier,
    config = {
      rowGap(8.dp)
      columnGap(8.dp)
      direction(FlexDirection.Row)
      wrap(FlexWrap.Wrap)
    },
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
        .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
        .padding(vertical = 4.dp, horizontal = 12.dp),
    color = MaterialTheme.colorScheme.onSurface,
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

    override suspend fun getLinkMetadata(url: String): LinkMetadata {
      return LinkMetadata("", "")
    }
  }

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
val TEST_POST =
  UIPost(
    shortId = "bdzmde",
    title =
      "I'm brave enough to say it: Linux is good now, and if you want to feel like you actually own your PC, make 2026 the year of Linux on (your) desktop",
    url =
      "https://www.pcgamer.com/software/linux/im-brave-enough-to-say-it-linux-is-good-now-and-if-you-want-to-feel-like-you-actually-own-your-pc-make-2026-the-year-of-linux-on-your-desktop",
    createdAt = "2026-01-02T09:49:33.000-06:00",
    commentCount = 100,
    commentsUrl = "https://lobste.rs/s/q1hh1g/simple_anomaly_detection_using_plain_sql",
    submitter = "rau",
    tags = listOf("linux", "games", "vibecoding", "concatenative"),
    description = "",
  )

@ThemePreviews
@Composable
private fun LobstersCardPreview() {
  LobstersTheme {
    LobstersCard(post = TEST_POST, isSaved = true, isRead = true, postActions = TEST_POST_ACTIONS)
  }
}
