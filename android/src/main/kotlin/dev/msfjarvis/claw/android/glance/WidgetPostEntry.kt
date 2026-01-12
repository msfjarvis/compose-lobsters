/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.model.UIPost

@Composable
@GlanceComposable
fun WidgetPostEntry(post: UIPost, modifier: GlanceModifier = GlanceModifier) {
  val titleStyle = MaterialTheme.typography.bodyMedium
  val commentsAction =
    actionStartActivity(
      Intent(
          Intent.ACTION_VIEW,
          "${BuildConfig.DEEPLINK_SCHEME}://comments/${post.shortId}".toUri(),
        )
        .apply {
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          setClass(LocalContext.current, MainActivity::class.java)
        }
    )
  val postAction =
    if (post.url.startsWith('/') || post.url.isEmpty()) commentsAction
    else actionStartActivity(Intent(Intent.ACTION_VIEW, post.url.toUri()))
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier =
      modifier.fillMaxWidth().cornerRadius(8.dp).background(GlanceTheme.colors.secondaryContainer),
  ) {
    Text(
      text = post.title,
      modifier =
        GlanceModifier.defaultWeight()
          .clickable(postAction)
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .padding(start = 4.dp),
      maxLines = 1,
      style =
        TextStyle(color = GlanceTheme.colors.onSecondaryContainer, fontSize = titleStyle.fontSize),
    )
    Image(
      provider = ImageProvider(R.drawable.ic_comment),
      contentDescription = "${post.commentCount} comments",
      colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
      modifier =
        GlanceModifier.clickable(commentsAction)
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .padding(horizontal = 4.dp),
    )
  }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@GlanceComposable
@Composable
private fun WidgetPostEntryPreview() {
  WidgetPostEntry(samplePosts(1).first())
}
