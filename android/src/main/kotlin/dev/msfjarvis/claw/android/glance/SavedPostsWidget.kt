/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.database.local.SavedPost
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

class SavedPostsWidget(
  private val savedPosts: Flow<List<SavedPost>>,
) : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent {
      GlanceTheme(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
        else ColorProviders(light = LightThemeColors, dark = DarkThemeColors)
      ) {
        WidgetHost()
      }
    }
  }

  @Composable
  private fun WidgetHost() {
    val posts by savedPosts.collectAsState(persistentListOf())

    LazyColumn(
      modifier =
        GlanceModifier.fillMaxSize()
          .background(GlanceTheme.colors.background)
          .appWidgetBackground(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      item {
        val style = MaterialTheme.typography.titleLarge
        Text(
          text = "Saved posts",
          style =
            TextStyle(
              color = GlanceTheme.colors.onBackground,
              fontSize = style.fontSize,
              fontWeight = style.fontWeight.toGlance(),
              fontStyle = style.fontStyle.toGlance(),
            ),
          modifier = GlanceModifier.padding(vertical = 8.dp),
        )
      }
      items(posts) { post -> WidgetListEntry(post = post) }
    }
  }
}
