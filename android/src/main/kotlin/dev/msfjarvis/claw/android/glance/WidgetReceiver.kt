/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.BroadcastReceiverKey

@ContributesIntoMap(AppScope::class, binding<BroadcastReceiver>())
@BroadcastReceiverKey(WidgetReceiver::class)
@Inject
class WidgetReceiver() : GlanceAppWidgetReceiver() {

  override val glanceAppWidget: GlanceAppWidget
    get() = SavedPostsWidget()

  override fun onReceive(context: Context, intent: Intent) {
    super.onReceive(context, intent)
  }
}
