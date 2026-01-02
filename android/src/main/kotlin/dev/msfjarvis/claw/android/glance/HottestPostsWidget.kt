/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Box
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.ClawApplication
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toUIPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class HottestPostsWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val appGraph = (context.applicationContext as ClawApplication).appGraph
    val posts =
      when (val postsResult = appGraph.lobstersApi.getHottestPosts(1)) {
        is ApiResult.Success -> postsResult.value.map(LobstersPost::toUIPost).toImmutableList()
        else -> persistentListOf()
      }
    provideContent { LobstersGlanceTheme { Content(posts) } }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    provideContent { Content(samplePosts()) }
  }

  @SuppressLint("ComposeUnstableReceiver")
  @Composable
  private fun Content(posts: ImmutableList<UIPost>, modifier: GlanceModifier = GlanceModifier) {
    WidgetContainer(
      "Hottest posts",
      listContent = {
        items(posts) { post ->
          Box(GlanceModifier.padding(horizontal = 16.dp, vertical = 4.dp)) { WidgetPostEntry(post) }
        }
      },
      modifier = modifier,
    )
  }

  @SuppressLint("VisibleForTests", "ComposeUnstableReceiver")
  @OptIn(ExperimentalGlancePreviewApi::class)
  @Preview
  @GlanceComposable
  @Composable
  private fun Preview() {
    Content(samplePosts())
  }
}
