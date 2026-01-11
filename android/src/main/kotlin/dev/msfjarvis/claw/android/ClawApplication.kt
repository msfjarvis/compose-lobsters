/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import android.os.Build
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import dev.msfjarvis.claw.android.glance.SavedPostsWidgetReceiver
import dev.msfjarvis.claw.android.injection.AppGraph
import dev.msfjarvis.claw.android.work.SavedPostUpdaterWorker
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClawApplication : Application(), MetroApplication {

  val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }

  override val appComponentProviders: MetroAppComponentProviders
    get() = appGraph

  @OptIn(DelicateCoroutinesApi::class)
  override fun onCreate() {
    super.onCreate()
    appGraph.plugins.forEach { plugin -> plugin.apply(this) }
    val postUpdateWorkRequest =
      PeriodicWorkRequestBuilder<SavedPostUpdaterWorker>(24, TimeUnit.HOURS)
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    appGraph.workManager.enqueueUniquePeriodicWork(
      uniqueWorkName = "updateSavedPosts",
      existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
      request = postUpdateWorkRequest,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      GlobalScope.launch(appGraph.mainDispatcher) {
        try {
          GlanceAppWidgetManager(applicationContext)
            .setWidgetPreviews(SavedPostsWidgetReceiver::class)
        } catch (_: IllegalArgumentException) {
          // Workaround for Motorola Android 15 bug where AppWidgetServiceImpl incorrectly
          // reports registered widgets as invalid when setting previews.
          // See: https://claw.sentry.io/share/issue/f8ee09821d8840b9b86293d3ffb627d3/
        }
      }
    }
  }
}
