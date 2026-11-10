/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.work.impl

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.msfjarvis.claw.core.work.api.WorkerInstanceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class InjectedWorkerFactory(
  private val workerProviders: Map<KClass<out ListenableWorker>, WorkerInstanceFactory<*>>
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ): ListenableWorker? {
    return registeredFactory(workerClassName)?.create(workerParameters)
  }

  internal fun registeredFactory(workerClassName: String): WorkerInstanceFactory<*>? =
    workerProviders[Class.forName(workerClassName).kotlin]
}
