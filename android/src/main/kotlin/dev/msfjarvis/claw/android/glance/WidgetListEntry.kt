/*
 * Copyright © 2023 Harsh Shandilya.
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
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.MainActivity.Companion.NAVIGATION_KEY
import dev.msfjarvis.claw.database.local.SavedPost

private val destinationKey = Key<String>(NAVIGATION_KEY)

@Composable
@GlanceComposable
fun WidgetListEntry(
  post: SavedPost,
  modifier: GlanceModifier = GlanceModifier,
) {
  val titleStyle = MaterialTheme.typography.titleMedium
  val subtitleStyle = MaterialTheme.typography.labelLarge
  val postAction =
    if (post.url.isNotEmpty()) {
      actionStartActivity(Intent(Intent.ACTION_VIEW, Uri.parse(post.url)))
    } else {
      actionStartActivity<MainActivity>(actionParametersOf(destinationKey to post.shortId))
    }
  val commentsAction =
    actionStartActivity<MainActivity>(actionParametersOf(destinationKey to post.shortId))
  Box(modifier.padding(8.dp)) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        GlanceModifier.fillMaxWidth()
          .background(GlanceTheme.colors.surfaceVariant)
          .cornerRadius(16.dp)
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
          )
      )
      post.commentCount?.let { count ->
        Text(
          text = "$count comments",
          modifier =
            GlanceModifier.wrapContentWidth().padding(end = 4.dp).clickable(commentsAction),
          style =
            TextStyle(
              color = GlanceTheme.colors.onSurfaceVariant,
              fontSize = subtitleStyle.fontSize,
              fontWeight = subtitleStyle.fontWeight.toGlance(),
              fontStyle = subtitleStyle.fontStyle.toGlance(),
              textAlign = TextAlign.End,
            )
        )
      }
    }
  }
}
