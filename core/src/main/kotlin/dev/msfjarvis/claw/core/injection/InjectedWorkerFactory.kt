/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.injection

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.collections.get
import kotlin.reflect.KClass

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class InjectedWorkerFactory(
  val workerProviders: Map<KClass<out ListenableWorker>, WorkerInstanceFactory<*>>
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ): ListenableWorker? {
    return workerProviders[Class.forName(workerClassName).kotlin]?.create(workerParameters)
  }

  interface WorkerInstanceFactory<T : ListenableWorker> {
    fun create(params: WorkerParameters): T
  }
}
