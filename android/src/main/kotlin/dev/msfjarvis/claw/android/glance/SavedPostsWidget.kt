/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.ClawApplication
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromSavedPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first

class SavedPostsWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val appGraph = (context.applicationContext as ClawApplication).appGraph
    val posts = appGraph.savedPostsRepository.getRecentPosts(50)
    val postWindow = posts.first().map(UIPost::fromSavedPost).toImmutableList()
    provideContent { WidgetHost(postWindow) }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    val appGraph = (context.applicationContext as ClawApplication).appGraph
    val posts = appGraph.savedPostsRepository.getRecentPosts(50)
    val postWindow = posts.first().map(UIPost::fromSavedPost).toImmutableList()
    provideContent { WidgetHost(postWindow) }
  }
}

@Composable
fun WidgetHost(posts: ImmutableList<UIPost>, modifier: GlanceModifier = GlanceModifier) {
  GlanceTheme(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
    else ColorProviders(light = LightThemeColors, dark = DarkThemeColors)
  ) {
    Scaffold(
      titleBar = {
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
          modifier = GlanceModifier.padding(8.dp),
        )
      },
      backgroundColor = GlanceTheme.colors.background,
    ) {
      LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        items(posts) { post -> WidgetListEntry(post = post) }
      }
    }
  }
}
