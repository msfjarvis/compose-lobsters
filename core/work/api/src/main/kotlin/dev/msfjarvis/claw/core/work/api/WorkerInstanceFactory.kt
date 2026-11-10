/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.work.api

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import dev.zacsweers.metro.MapKey
import kotlin.reflect.KClass

/** Creates a registered worker with the parameters supplied by WorkManager. */
fun interface WorkerInstanceFactory<T : ListenableWorker> {
  fun create(params: WorkerParameters): T
}

/** Identifies the worker class created by a [WorkerInstanceFactory] multibinding. */
@MapKey
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
