/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.updates

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.viewmodel.InAppUpdateViewModel
import dev.zacsweers.metrox.viewmodel.metroViewModel

/**
 * Drives Google Play in-app updates for the host screen.
 *
 * Checks for updates whenever the app comes to the foreground, starts the update flow through Play,
 * and surfaces a snackbar asking the user to restart once a flexible update has been downloaded.
 */
@Composable
fun InAppUpdates(
  snackbarHostState: SnackbarHostState,
  viewModel: InAppUpdateViewModel = metroViewModel(),
) {
  val restartMessage = stringResource(R.string.in_app_update_ready)
  val restartActionLabel = stringResource(R.string.in_app_update_restart)
  val launcher =
    rememberLauncherForActivityResult(StartIntentSenderForResult()) { result ->
      viewModel.onUpdateFlowResult(result.resultCode)
    }

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.checkForUpdate() }

  LaunchedEffect(viewModel) {
    viewModel.startUpdateEvents.collect { event -> viewModel.startUpdate(event, launcher) }
  }

  LaunchedEffect(viewModel, restartMessage, restartActionLabel) {
    viewModel.restartPrompts.collect {
      val result =
        snackbarHostState.showSnackbar(
          message = restartMessage,
          actionLabel = restartActionLabel,
          withDismissAction = true,
          duration = SnackbarDuration.Indefinite,
        )
      if (result == SnackbarResult.ActionPerformed) {
        viewModel.completeUpdate()
      }
    }
  }
}
