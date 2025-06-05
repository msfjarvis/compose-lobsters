/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.model.Tag
import java.io.IOException
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ContributesViewModel
class TagFilterViewModel
@Inject
constructor(
  private val api: LobstersApi,
  private val tagFilterRepository: TagFilterRepository,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

  var filteredTags by mutableStateOf<ImmutableList<String>>(persistentListOf())
    private set

  var allTags by mutableStateOf<NetworkState>(NetworkState.Loading)
    private set

  init {
    viewModelScope.launch {
      tagFilterRepository.getSavedTags().collectLatest { filteredTags = it.toImmutableList() }
    }
    viewModelScope.launch {
      allTags =
        runSuspendCatching<ImmutableList<Tag>> {
            withContext(ioDispatcher) {
              when (val result = api.getTags()) {
                is Success -> result.value.toImmutableList()
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

  fun saveTags(tags: Set<String>) {
    viewModelScope.launch { tagFilterRepository.saveTags(tags) }
  }
}
