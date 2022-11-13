/*
 * Copyright © 2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager
import com.squareup.anvil.annotations.ContributesMultibinding
import dev.msfjarvis.claw.core.injection.AppPlugin
import dev.msfjarvis.claw.injection.scopes.AppScope
import javax.inject.Inject
import tangle.work.TangleWorkerFactory

@ContributesMultibinding(AppScope::class)
class WorkManagerPlugin
@Inject
constructor(
  private val workerFactory: TangleWorkerFactory,
) : AppPlugin {
  override fun apply(application: Application) {
    WorkManager.initialize(
      application,
      Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .setMinimumLoggingLevel(Log.DEBUG)
        .build()
    )
  }
}
