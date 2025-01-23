/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.broadcastreceiver.ContributesBroadcastReceiverInjector
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import javax.inject.Inject

@ContributesBroadcastReceiverInjector
class WidgetReceiver : GlanceAppWidgetReceiver() {

  @Inject lateinit var viewModel: ClawViewModel

  override val glanceAppWidget: GlanceAppWidget
    get() = SavedPostsWidget(viewModel.savedPosts)

  override fun onReceive(context: Context, intent: Intent) {
    Whetstone.inject(this, context)
    super.onReceive(context, intent)
  }
}
