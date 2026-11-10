/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Context
import androidx.glance.appwidget.updateAll
import dev.msfjarvis.claw.core.coroutines.MainDispatcher
import dev.msfjarvis.claw.feature.widgets.api.WidgetRefresher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@ContributesBinding(AppScope::class, binding = binding<WidgetRefresher>())
class AndroidWidgetRefresher(
  private val context: Context,
  @param:MainDispatcher private val mainDispatcher: CoroutineDispatcher,
) : WidgetRefresher {
  override suspend fun refreshSavedPosts() {
    withContext(mainDispatcher) { SavedPostsWidget().updateAll(context) }
  }
}
