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
import androidx.glance.GlanceComposable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
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
