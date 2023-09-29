/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontStyle as UIFontStyle
import androidx.compose.ui.text.font.FontWeight as UIFontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters.Key
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
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
  Box(modifier.padding(8.dp)) {
    Column(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
        GlanceModifier.fillMaxWidth()
          .background(GlanceTheme.colors.surfaceVariant)
          .cornerRadius(8.dp)
          .padding(horizontal = 8.dp)
          .clickable(
            actionStartActivity<MainActivity>(actionParametersOf(destinationKey to post.shortId))
          ),
    ) {
      Text(
        text = post.title,
        modifier = GlanceModifier.padding(horizontal = 4.dp, vertical = 4.dp),
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
            GlanceModifier.defaultWeight()
              .padding(horizontal = 4.dp, vertical = 4.dp)
              .fillMaxWidth(),
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

fun UIFontWeight?.toGlance(): FontWeight? {
  return when (this) {
    UIFontWeight.Normal -> FontWeight.Normal
    UIFontWeight.Medium -> FontWeight.Medium
    UIFontWeight.Bold -> FontWeight.Bold
    else -> null
  }
}

fun UIFontStyle?.toGlance(): FontStyle? {
  return when (this) {
    UIFontStyle.Normal -> FontStyle.Normal
    UIFontStyle.Italic -> FontStyle.Italic
    else -> null
  }
}
