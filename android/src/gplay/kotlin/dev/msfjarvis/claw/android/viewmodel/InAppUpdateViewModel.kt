/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import io.sentry.Sentry
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Update priority at or above which an immediate update is requested. Play models update priority
 * as an integer between 0 and 5, with 5 being the most urgent.
 */
private const val IMMEDIATE_UPDATE_PRIORITY_THRESHOLD = 4

/**
 * Emitted when Play reports an update that the UI should start through
 * [InAppUpdateViewModel.startUpdate].
 */
data class StartUpdateEvent(val updateInfo: AppUpdateInfo, val updateType: Int)

@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class InAppUpdateViewModel(private val appUpdateManager: AppUpdateManager) : ViewModel() {

  // A channel rather than a SharedFlow so a request is not dropped if the UI collector is briefly
  // unavailable, such as while the activity is being recreated.
  private val _startUpdateEvents = Channel<StartUpdateEvent>(Channel.BUFFERED)

  /** Emits whenever the UI should ask Play to start an update flow. */
  val startUpdateEvents: Flow<StartUpdateEvent> = _startUpdateEvents.receiveAsFlow()

  private val _restartPrompts =
    MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /** Emits whenever a downloaded update is waiting for the user to restart the app. */
  val restartPrompts: SharedFlow<Unit> = _restartPrompts.asSharedFlow()

  /**
   * Tracks whether the current process has already asked the user to update. Prevents the update
   * dialog from reappearing on every resume after the user dismisses it.
   */
  private var hasRequestedUpdate = false

  private val installStateListener = InstallStateUpdatedListener { state ->
    if (state.installStatus() == InstallStatus.DOWNLOADED) {
      _restartPrompts.tryEmit(Unit)
    } else if (state.installStatus() == InstallStatus.FAILED) {
      // Let the next foreground check offer the update again.
      hasRequestedUpdate = false
      Sentry.captureMessage("In-app update download failed")
    }
  }

  init {
    appUpdateManager.registerListener(installStateListener)
  }

  override fun onCleared() {
    appUpdateManager.unregisterListener(installStateListener)
    _startUpdateEvents.close()
  }

  /**
   * Checks Play for an available update. Call this from every app entry point, most importantly
   * whenever the app comes to the foreground so interrupted updates are resumed.
   */
  fun checkForUpdate() {
    appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
      when {
        updateInfo.updateAvailability() ==
          UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
          // An immediate update was interrupted, resume it.
          _startUpdateEvents.trySend(StartUpdateEvent(updateInfo, AppUpdateType.IMMEDIATE))
        }
        updateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
          _restartPrompts.tryEmit(Unit)
        }
        !hasRequestedUpdate &&
          updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE -> {
          val updateType =
            when {
              updateInfo.updatePriority() >= IMMEDIATE_UPDATE_PRIORITY_THRESHOLD &&
                updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
              updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> AppUpdateType.FLEXIBLE
              updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> AppUpdateType.IMMEDIATE
              else -> null
            }
          if (updateType != null) {
            hasRequestedUpdate = true
            if (_startUpdateEvents.trySend(StartUpdateEvent(updateInfo, updateType)).isFailure) {
              hasRequestedUpdate = false
            }
          }
        }
      }
    }
  }

  /**
   * Starts the update flow for [event] using the given [launcher]. Must be called from a scope that
   * is tied to the activity's lifecycle so the launcher is always valid.
   */
  fun startUpdate(event: StartUpdateEvent, launcher: ActivityResultLauncher<IntentSenderRequest>) {
    val started =
      appUpdateManager.startUpdateFlowForResult(
        event.updateInfo,
        launcher,
        AppUpdateOptions.newBuilder(event.updateType).build(),
      )
    if (!started) {
      hasRequestedUpdate = false
    }
  }

  /**
   * Records the outcome of an update flow that was started via [startUpdate]. A cancelled flow
   * intentionally does not reset the prompt guard so the user is not asked again in this process.
   */
  fun onUpdateFlowResult(resultCode: Int) {
    if (resultCode != Activity.RESULT_OK && resultCode != Activity.RESULT_CANCELED) {
      Sentry.captureMessage("In-app update flow failed with result code $resultCode")
    }
  }

  /** Restarts the app to install a flexible update that has finished downloading. */
  fun completeUpdate() {
    appUpdateManager.completeUpdate().addOnFailureListener { exception ->
      Sentry.captureException(exception)
    }
  }
}
