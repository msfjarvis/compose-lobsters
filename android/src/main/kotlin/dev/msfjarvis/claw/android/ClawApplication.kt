/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import dev.msfjarvis.claw.android.injection.AppGraph
import dev.msfjarvis.claw.android.work.SavedPostUpdaterWorker
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class ClawApplication : Application(), MetroApplication, Configuration.Provider {

  private val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }
  override val appComponentProviders: MetroAppComponentProviders
    get() = appGraph

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()

  override fun onCreate() {
    super.onCreate()
    appGraph.plugins.forEach { plugin -> plugin.apply(this) }
    val postUpdateWorkRequest =
      OneTimeWorkRequestBuilder<SavedPostUpdaterWorker>()
        .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
        .build()
    appGraph.workManager.enqueueUniqueWork(
      uniqueWorkName = "updateSavedPosts",
      existingWorkPolicy = ExistingWorkPolicy.KEEP,
      request = postUpdateWorkRequest,
    )
  }

  private companion object {
    private const val POST_REFRESH_PERIOD = 24L // 24 hours
  }
}
