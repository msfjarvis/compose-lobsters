/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.msfjarvis.claw.android.injection.InjectedWorkerFactory
import dev.msfjarvis.claw.android.injection.WorkerKey
import dev.msfjarvis.claw.android.reminders.DailySavedPostReminderDelivery
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding

/** Delivers the persisted daily saved-post snapshot, if reminders are enabled and available. */
@AssistedInject
class DailySavedPostReminderWorker(
  context: Context,
  @Assisted params: WorkerParameters,
  private val delivery: DailySavedPostReminderDelivery,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result = delivery.deliver()

  @WorkerKey(DailySavedPostReminderWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<InjectedWorkerFactory.WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory : InjectedWorkerFactory.WorkerInstanceFactory<DailySavedPostReminderWorker>
}
