/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.ClawApplication
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromSavedPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first

class SavedPostsWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val appGraph = (context.applicationContext as ClawApplication).appGraph
    val posts = appGraph.savedPostsRepository.getRecentPosts(50)
    val postWindow = posts.first().map(UIPost::fromSavedPost).toImmutableList()
    provideContent { ThemedView { WidgetHost(postWindow) } }
  }

  @SuppressLint("VisibleForTests")
  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    provideContent {
      ThemedView { WidgetHost(buildList(5) { repeat(5) { add(TEST_POST) } }.toPersistentList()) }
    }
  }
}

@Composable
internal fun ThemedView(content: @GlanceComposable @Composable () -> Unit) {
  GlanceTheme(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
    else ColorProviders(light = LightThemeColors, dark = DarkThemeColors),
    content,
  )
}

@Composable
fun WidgetHost(posts: ImmutableList<UIPost>, modifier: GlanceModifier = GlanceModifier) {
  Column(
    modifier.fillMaxSize().background(GlanceTheme.colors.widgetBackground).cornerRadius(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = GlanceModifier.padding(16.dp).fillMaxWidth(),
    ) {
      Image(
        provider = ImageProvider(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
        modifier = GlanceModifier.size(24.dp),
      )
      Spacer(GlanceModifier.width(16.dp))
      Text("Saved posts", style = TextStyle(fontSize = 18.sp, color = GlanceTheme.colors.onSurface))
    }
    LazyColumn {
      items(posts) { post ->
        Box(GlanceModifier.padding(horizontal = 16.dp, vertical = 4.dp)) { WidgetListEntry(post) }
      }
    }
  }
}

@SuppressLint("VisibleForTests")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@GlanceComposable
@Composable
private fun Preview() {
  ThemedView { WidgetHost(buildList(5) { repeat(5) { add(TEST_POST) } }.toPersistentList()) }
}
