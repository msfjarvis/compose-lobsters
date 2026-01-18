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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.glance.GlanceComposable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
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
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.BuildConfig
import dev.msfjarvis.claw.android.ClawApplication
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.database.local.CachedRemotePost
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.fromCachedRemotePost
import dev.msfjarvis.claw.model.toUIPost
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first

class HottestPostsWidget : GlanceAppWidget() {

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val appGraph = (context.applicationContext as ClawApplication).appGraph
    val cachedRemotePostsRepository = appGraph.cachedRemotePostsRepository
    val tagFilterRepository = appGraph.tagFilterRepository
    val filteredTags = try {
      tagFilterRepository.getSavedTags().first()
    } catch (_: Exception) {
      emptySet()
    }
    val posts =
      when (val postsResult = appGraph.lobstersApi.getHottestPosts(1)) {
        is ApiResult.Success -> {
          val uiPosts = postsResult.value.map(LobstersPost::toUIPost)
          // Cache the posts for future use when network is unavailable
          // Using try-catch to prevent widget rendering failures if caching fails
          try {
            val cachedPosts =
              uiPosts.mapIndexed { index, post ->
                CachedRemotePost(
                  shortId = post.shortId,
                  title = post.title,
                  url = post.url,
                  createdAt = post.createdAt,
                  commentCount = post.commentCount,
                  commentsUrl = post.commentsUrl,
                  submitterName = post.submitter,
                  tags = post.tags,
                  description = post.description,
                  userIsAuthor = post.userIsAuthor,
                  insertionOrder = index,
                )
              }
            cachedRemotePostsRepository.savePosts(cachedPosts)
          } catch (_: Exception) {
            // Silently ignore caching failures - widget should still render
          }
          uiPosts.toImmutableList()
        }
        else -> {
          // Fall back to cached posts when network fails
          cachedRemotePostsRepository
            .getCachedPosts()
            .map(UIPost::fromCachedRemotePost)
            .toImmutableList()
        }
      }
    // Filter out posts with tags that are in the filtered tags set
    val filteredPosts = if (filteredTags.isEmpty()) {
      posts
    } else {
      posts.filter { post -> post.tags.none { tag -> filteredTags.contains(tag) } }.toImmutableList()
    }
    provideContent { LobstersGlanceTheme { Content(filteredPosts) } }
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
        item {
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
                      Intent(Intent.ACTION_VIEW, "${BuildConfig.DEEPLINK_SCHEME}://hottest".toUri())
                        .apply {
                          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                          setClass(LocalContext.current, MainActivity::class.java)
                        }
                    )
                  ),
            ) {
              Text(
                text = "See more posts",
                style =
                  TextStyle(color = GlanceTheme.colors.onPrimary, textAlign = TextAlign.Center),
              )
            }
          }
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
