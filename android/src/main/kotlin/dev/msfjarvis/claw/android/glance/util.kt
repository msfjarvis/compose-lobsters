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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceComposable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.LazyListScope
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
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
      modifier = GlanceModifier.padding(16.dp).fillMaxWidth(),
    ) {
      Image(
        provider = ImageProvider(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
        modifier = GlanceModifier.size(36.dp),
      )
      Spacer(GlanceModifier.width(16.dp))
      Text(text = title, style = TextStyle(fontSize = 18.sp, color = GlanceTheme.colors.onSurface))
    }
    LazyColumn(content = listContent)
  }
}

@SuppressLint("VisibleForTests")
fun samplePosts() = buildList(5) { repeat(5) { add(TEST_POST) } }.toPersistentList()

fun FontWeight?.toGlance(): androidx.glance.text.FontWeight? {
  return when (this) {
    FontWeight.Normal -> androidx.glance.text.FontWeight.Normal
    FontWeight.Medium -> androidx.glance.text.FontWeight.Medium
    FontWeight.Bold -> androidx.glance.text.FontWeight.Bold
    else -> null
  }
}

fun FontStyle?.toGlance(): androidx.glance.text.FontStyle? {
  return when (this) {
    FontStyle.Normal -> androidx.glance.text.FontStyle.Normal
    FontStyle.Italic -> androidx.glance.text.FontStyle.Italic
    else -> null
  }
}
