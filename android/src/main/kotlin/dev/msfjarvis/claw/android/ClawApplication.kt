/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import dev.msfjarvis.claw.android.injection.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class ClawApplication : Application(), MetroApplication {

  val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

  override val appComponentProviders: MetroAppComponentProviders
    get() = appGraph

  override fun onCreate() {
    super.onCreate()
    appGraph.plugins.forEach { plugin -> plugin.apply(this) }
    appGraph.workManagerRequests.forEach { it.enqueue() }
    appGraph.dailySavedPostReminderScheduler.reconcile()
  }
}
