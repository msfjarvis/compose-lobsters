/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExperimentalEventsApi
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import dev.msfjarvis.claw.android.work.WorkManagerExecutionTelemetry
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@ContributesIntoSet(AppScope::class)
@Inject
class WorkManagerPlugin(private val workerFactory: WorkerFactory) : AppPlugin {
  @OptIn(ExperimentalEventsApi::class)
  override fun apply(application: Application) {
    WorkManager.initialize(
      application,
      Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setExecutionEventListener(WorkManagerExecutionTelemetry())
        .setMinimumLoggingLevel(Log.DEBUG)
        .build(),
    )
  }
}
