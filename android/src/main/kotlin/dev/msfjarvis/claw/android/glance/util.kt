/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun LobstersGlanceTheme(content: @GlanceComposable @Composable () -> Unit) {
  GlanceTheme(
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors
    else ColorProviders(light = LightThemeColors, dark = DarkThemeColors),
    content,
  )
}

@Composable
fun WidgetContainer(
  title: String,
  listContent: LazyListScope.() -> Unit,
  modifier: GlanceModifier = GlanceModifier,
) {
  Column(
    modifier.fillMaxSize().background(GlanceTheme.colors.widgetBackground).cornerRadius(16.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = GlanceModifier.padding(16.dp).fillMaxWidth(),
    ) {
      Text(text = title, style = TextStyle(fontSize = 18.sp, color = GlanceTheme.colors.onSurface))
    }
    LazyColumn(content = listContent)
  }
}

@SuppressLint("VisibleForTests")
fun samplePosts(
  count: Int = 5,
  mutator: UIPost.(idx: Int) -> UIPost = { this },
): PersistentList<UIPost> {
  return buildList(count) { repeat(count) { add(TEST_POST.mutator(it)) } }.toPersistentList()
}

internal fun generatedWidgetPreviewPosts(): PersistentList<UIPost> {
  return listOf(
      TEST_POST.withPrimaryStory {
          copy(
            shortId = "widget-preview-1",
            title = "Rust 2026 roadmap",
            url = "/s/widget_preview_1",
            submitter = "alice",
            tags = ["rust", "roadmap"],
          )
        }
        .copy(
          commentCount = 18,
          commentsUrl = "https://lobste.rs/s/widget_preview_1/rust_2026_roadmap",
          description = "",
        ),
      TEST_POST.withPrimaryStory {
          copy(
            shortId = "widget-preview-2",
            title = "SQLite tips for mobile apps",
            url = "/s/widget_preview_2",
            submitter = "bob",
            tags = ["sqlite", "android"],
          )
        }
        .copy(
          commentCount = 7,
          commentsUrl = "https://lobste.rs/s/widget_preview_2/sqlite_tips_for_mobile_apps",
          description = "",
        ),
    )
    .toPersistentList()
}

internal fun UIPost.withPrimaryStory(update: SourceStory.() -> SourceStory): UIPost =
  copy(discussion = discussion.copy(stories = listOf(update(discussion.first))))

internal fun generatedWidgetPreviewDelayMinutes(): Long = 15L

internal const val WidgetFooterItemId: Long = 4_611_686_018_427_387_904L

private const val MAX_WIDGET_POST_ITEM_ID = 4_611_686_018_427_387_903L

internal fun widgetPostItemId(shortId: String): Long {
  val digest = MessageDigest.getInstance("SHA-256").digest(shortId.toByteArray(Charsets.UTF_8))
  return ByteBuffer.wrap(digest).long and MAX_WIDGET_POST_ITEM_ID
}
