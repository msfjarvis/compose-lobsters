/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import com.squareup.anvil.annotations.optional.ForScope
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.core.injection.IODispatcher
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.model.toUIPost
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@ContributesViewModel
class CommentsViewModel
@Inject
constructor(
  private val api: LobstersApi,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
  @ForScope(ApplicationScope::class) context: Context,
) : AndroidViewModel(context as Application) {
  var postDetails by mutableStateOf<NetworkState>(NetworkState.Loading)

  suspend fun loadPostDetails(postId: String) {
    postDetails =
      runSuspendCatching<UIPost> {
          withContext(ioDispatcher) {
            when (val result = api.getPostDetails(postId)) {
              is Success -> result.value.toUIPost()
              is Failure.NetworkFailure -> throw result.error
              is Failure.UnknownFailure -> throw result.error
              is Failure.HttpFailure -> throw result.toError()
              is Failure.ApiFailure -> throw IOException("API returned an invalid response")
            }
          }
        }
        .fold(
          success = { details -> NetworkState.Success(details) },
          failure = { NetworkState.Error(error = it, description = "Failed to load comments") },
        )
  }
}
