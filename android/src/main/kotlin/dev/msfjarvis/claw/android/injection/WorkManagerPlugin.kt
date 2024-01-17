/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import dev.msfjarvis.claw.core.injection.AppPlugin
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class WorkManagerPlugin @Inject constructor(private val workerFactory: WorkerFactory) : AppPlugin {
  override fun apply(application: Application) {
    WorkManager.initialize(
      application,
      Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(Log.DEBUG)
        .build(),
    )
  }
}
