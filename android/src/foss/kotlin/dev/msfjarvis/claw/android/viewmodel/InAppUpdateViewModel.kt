/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Event placeholder that keeps the FOSS source set independent of Play Core. */
data class StartUpdateEvent(val updateType: Int = 0)

class InAppUpdateViewModel(@Suppress("UNUSED_PARAMETER") appUpdateManager: AppUpdateManager) :
  ViewModel() {
  val startUpdateEvents: Flow<StartUpdateEvent> = emptyFlow()
  val restartPrompts: Flow<Unit> = emptyFlow()

  fun checkForUpdate() {}

  fun startUpdate(
    @Suppress("UNUSED_PARAMETER") event: StartUpdateEvent,
    @Suppress("UNUSED_PARAMETER") launcher: ActivityResultLauncher<IntentSenderRequest>,
  ) {}

  fun onUpdateFlowResult(@Suppress("UNUSED_PARAMETER") resultCode: Int) {}

  fun completeUpdate() {}
}
