/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.work.impl

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExperimentalEventsApi
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import dev.zacsweers.metro.Inject

/**
 * Initializes WorkManager with the application's injected worker factory and execution telemetry.
 */
@Inject
class WorkManagerInitializer(private val workerFactory: WorkerFactory) {
  fun initialize(application: Application) {
    WorkManager.initialize(application, createConfiguration())
  }

  @OptIn(ExperimentalEventsApi::class)
  internal fun createConfiguration(): Configuration =
    Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .setExecutionEventListener(WorkManagerExecutionTelemetry())
      .setMinimumLoggingLevel(Log.DEBUG)
      .build()
}
