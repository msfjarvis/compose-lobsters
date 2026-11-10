/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.work.impl

import androidx.work.ListenableWorker
import dev.msfjarvis.claw.core.work.api.WorkManagerRequests
import dev.msfjarvis.claw.core.work.api.WorkerInstanceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass

@ContributesTo(AppScope::class)
interface WorkerBindings {
  @Multibinds val workManagerRequests: Set<WorkManagerRequests>

  @Multibinds val workerProviders: Map<KClass<out ListenableWorker>, () -> WorkerInstanceFactory<*>>
}
