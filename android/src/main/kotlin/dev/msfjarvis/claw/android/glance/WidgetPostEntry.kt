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
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.model.UIPost

@Composable
@GlanceComposable
fun WidgetPostEntry(post: UIPost, modifier: GlanceModifier = GlanceModifier) {
  val titleStyle = MaterialTheme.typography.titleMedium
  val commentsAction =
    actionStartActivity(
      Intent(Intent.ACTION_VIEW, "claw://comments/${post.shortId}".toUri()).apply {
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
      modifier
        .fillMaxWidth()
        .background(GlanceTheme.colors.surfaceVariant)
        .padding(horizontal = 8.dp, vertical = 4.dp),
  ) {
    Text(
      text = post.title,
      modifier = GlanceModifier.defaultWeight().padding(start = 4.dp).clickable(postAction),
      style =
        TextStyle(
          color = GlanceTheme.colors.onSurfaceVariant,
          fontSize = titleStyle.fontSize,
          fontWeight = titleStyle.fontWeight.toGlance(),
          fontStyle = titleStyle.fontStyle.toGlance(),
        ),
    )
    Image(
      provider = ImageProvider(R.drawable.ic_comment),
      contentDescription = "${post.commentCount} comments",
      modifier = GlanceModifier.padding(end = 4.dp).clickable(commentsAction),
    )
  }
}
