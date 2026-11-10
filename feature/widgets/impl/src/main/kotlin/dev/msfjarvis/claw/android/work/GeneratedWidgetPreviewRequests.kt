/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.work

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import dev.msfjarvis.claw.core.work.api.WorkManagerRequests
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject

@Inject
@ContributesIntoSet(AppScope::class)
class GeneratedWidgetPreviewRequests(private val workManager: WorkManager) : WorkManagerRequests {
  override fun enqueue() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      enqueuePreviewUpdate()
    }
  }

  @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
  private fun enqueuePreviewUpdate() = GeneratedWidgetPreviewUpdateWorker.enqueue(workManager)
}
