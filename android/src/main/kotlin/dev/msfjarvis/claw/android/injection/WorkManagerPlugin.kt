/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.injection

import android.app.Application
import dev.msfjarvis.claw.core.work.impl.WorkManagerInitializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

/** Connects generic WorkManager initialization to the app's plugin lifecycle. */
@ContributesIntoSet(AppScope::class)
@Inject
class WorkManagerPlugin(private val initializer: WorkManagerInitializer) : AppPlugin {
  override fun apply(application: Application) = initializer.initialize(application)
}
