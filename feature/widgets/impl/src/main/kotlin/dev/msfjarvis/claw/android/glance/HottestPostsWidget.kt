/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.feature.widgets.impl.BuildConfig
import dev.msfjarvis.claw.feature.widgets.impl.R
import dev.msfjarvis.claw.feature.widgets.impl.widgetDependencies
import dev.msfjarvis.claw.model.UIPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class HottestPostsWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val dependencies = widgetDependencies(context)
    val filteredTags =
      try {
        dependencies.widgetTagFilter.getSavedTagsSnapshot()
      } catch (_: Exception) {
        emptySet()
      }
    val posts = dependencies.hottestPosts.getHottestPosts().toImmutableList()
    // Filter out posts with tags that are in the filtered tags set
    val filteredPosts =
      if (filteredTags.isEmpty()) {
        posts
      } else {
        posts
          .filter { post -> post.tags.none { tag -> filteredTags.contains(tag) } }
          .toImmutableList()
      }
    provideContent { LobstersGlanceTheme { Content(context, filteredPosts) } }
  }

  override suspend fun providePreview(context: Context, widgetCategory: Int) {
    provideContent {
      LobstersGlanceTheme { PreviewContent(context, generatedWidgetPreviewPosts()) }
    }
  }

  @SuppressLint("ComposeUnstableReceiver")
  @Composable
  @NonRestartableComposable
  private fun Content(
    context: Context,
    posts: ImmutableList<UIPost>,
    modifier: GlanceModifier = GlanceModifier,
    actionsEnabled: Boolean = true,
    showFooterAction: Boolean = true,
  ) {
    WidgetContainer(
      "Hottest posts",
      listContent = {
        items(posts, itemId = { post -> widgetPostItemId(post.shortId) }) { post ->
          Box(GlanceModifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
            WidgetPostEntry(post, actionsEnabled = actionsEnabled)
          }
        }
        if (showFooterAction) {
          item(itemId = WidgetFooterItemId) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = GlanceModifier.fillMaxWidth().padding(16.dp),
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier =
                  GlanceModifier.background(GlanceTheme.colors.primary)
                    .cornerRadius(20.dp)
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clickable(
                      actionStartActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "${BuildConfig.DEEPLINK_SCHEME}://hottest".toUri(),
                          )
                          .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                          }
                      )
                    ),
              ) {
                Text(
                  text = context.getString(R.string.see_more_posts),
                  style =
                    TextStyle(color = GlanceTheme.colors.onPrimary, textAlign = TextAlign.Center),
                )
              }
            }
          }
        }
      },
      modifier = modifier,
    )
  }

  @SuppressLint("ComposeUnstableReceiver")
  @Composable
  @NonRestartableComposable
  private fun PreviewContent(
    context: Context,
    posts: ImmutableList<UIPost>,
    modifier: GlanceModifier = GlanceModifier,
  ) {
    Content(
      context = context,
      posts = posts,
      modifier = modifier,
      actionsEnabled = false,
      showFooterAction = false,
    )
  }
}
