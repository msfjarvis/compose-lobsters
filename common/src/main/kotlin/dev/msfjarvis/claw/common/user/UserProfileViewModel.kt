/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.slack.eithernet.ApiResult
import com.slack.eithernet.ApiResult.Failure
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.common.NetworkState.Error
import dev.msfjarvis.claw.common.NetworkState.Loading
import dev.msfjarvis.claw.common.NetworkState.Success
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.model.User
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
@ViewModelKey(UserProfileViewModel::class)
@ContributesIntoMap(AppScope::class)
class UserProfileViewModel(
  private val api: LobstersApi,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

  var userProfile by mutableStateOf<NetworkState>(Loading)

  suspend fun loadProfile(username: String) {
    userProfile =
      runSuspendCatching<User> {
          withContext(ioDispatcher) {
            when (val result = api.getUser(username)) {
              is ApiResult.Success -> result.value
              is Failure.NetworkFailure -> throw result.error
              is Failure.UnknownFailure -> throw result.error
              is Failure.HttpFailure -> throw result.toError()
              is Failure.ApiFailure -> throw IOException("API returned an invalid response")
            }
          }
        }
        .fold(
          success = { profile -> Success(profile) },
          failure = { Error(error = it, description = "Failed to load profile for $username") },
        )
  }
}
