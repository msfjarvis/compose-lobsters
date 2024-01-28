/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters.Key
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.MainActivity.Companion.NAVIGATION_KEY
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.model.UIPost

private val destinationKey = Key<String>(NAVIGATION_KEY)

@Composable
@GlanceComposable
fun WidgetListEntry(post: UIPost, modifier: GlanceModifier = GlanceModifier) {
  val titleStyle = MaterialTheme.typography.titleMedium
  val commentsAction =
    actionStartActivity<MainActivity>(actionParametersOf(destinationKey to post.shortId))
  val postAction =
    if (post.url.isNotEmpty()) actionStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.url)))
    else commentsAction
  Box(modifier.padding(8.dp)) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        GlanceModifier.fillMaxWidth()
          .background(GlanceTheme.colors.surfaceVariant)
          .cornerRadius(4.dp)
          .padding(8.dp),
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
}
