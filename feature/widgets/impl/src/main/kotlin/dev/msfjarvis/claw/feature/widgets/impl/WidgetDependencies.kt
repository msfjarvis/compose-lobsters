/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.widgets.impl

import dev.msfjarvis.claw.feature.posts.api.HottestPostsSource
import dev.msfjarvis.claw.feature.posts.api.SavedPosts
import dev.zacsweers.metrox.android.MetroApplication

/** App-owned dependencies needed by Android widget entry points. */
interface WidgetDependencies {
  val savedPostsRepository: SavedPosts
  val hottestPosts: HottestPostsSource
  val widgetTagFilter: WidgetTagFilter
}

fun interface WidgetTagFilter {
  suspend fun getSavedTagsSnapshot(): Set<String>
}

internal fun widgetDependencies(application: android.content.Context): WidgetDependencies =
  ((application.applicationContext as MetroApplication).appComponentProviders as WidgetDependencies)
