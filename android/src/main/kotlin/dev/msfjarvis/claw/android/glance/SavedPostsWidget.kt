/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.msfjarvis.claw.database.local.SavedPost
import java.time.Month
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow

class SavedPostsWidget(
  private val savedPosts: Flow<ImmutableMap<Month, List<SavedPost>>>,
) : GlanceAppWidget() {
  override suspend fun provideGlance(context: Context, id: GlanceId) {
    provideContent { MyContent() }
  }

  @Composable
  private fun MyContent() {
    val posts by savedPosts.collectAsState(persistentMapOf())

    LazyColumn(
      modifier = GlanceModifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      items(posts.values.flatten()) { post -> Text(post.title, style = TextStyle()) }
    }
  }
}
