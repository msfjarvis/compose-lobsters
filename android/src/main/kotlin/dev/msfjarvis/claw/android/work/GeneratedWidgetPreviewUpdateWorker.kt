/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dev.msfjarvis.claw.android.glance.SavedPostsWidgetReceiver
import dev.msfjarvis.claw.android.glance.generatedWidgetPreviewDelayMinutes
import dev.msfjarvis.claw.android.injection.InjectedWorkerFactory
import dev.msfjarvis.claw.android.injection.WorkerKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import java.util.concurrent.TimeUnit

@AssistedInject
class GeneratedWidgetPreviewUpdateWorker(
  context: Context,
  @Assisted params: WorkerParameters,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return try {
      GlanceAppWidgetManager(applicationContext).setWidgetPreviews(SavedPostsWidgetReceiver::class)
      Result.success()
    } catch (_: IllegalArgumentException) {
      // Workaround for Motorola Android 15 bug where AppWidgetServiceImpl incorrectly
      // reports registered widgets as invalid when setting previews.
      // See: https://claw.sentry.io/share/issue/f8ee09821d8840b9b86293d3ffb627d3/
      Result.success()
    }
  }

  companion object {
    const val UNIQUE_WORK_NAME = "generatedWidgetPreviewUpdate"

    fun enqueue(workManager: WorkManager) {
      workManager.enqueueUniqueWork(
        UNIQUE_WORK_NAME,
        androidx.work.ExistingWorkPolicy.KEEP,
        request(),
      )
    }

    fun request(): OneTimeWorkRequest {
      return OneTimeWorkRequestBuilder<GeneratedWidgetPreviewUpdateWorker>()
        .setInitialDelay(generatedWidgetPreviewDelayMinutes(), TimeUnit.MINUTES)
        .build()
    }
  }

  @WorkerKey(GeneratedWidgetPreviewUpdateWorker::class)
  @ContributesIntoMap(
    AppScope::class,
    binding = binding<InjectedWorkerFactory.WorkerInstanceFactory<*>>(),
  )
  @AssistedFactory
  abstract class Factory :
    InjectedWorkerFactory.WorkerInstanceFactory<GeneratedWidgetPreviewUpdateWorker>
}
