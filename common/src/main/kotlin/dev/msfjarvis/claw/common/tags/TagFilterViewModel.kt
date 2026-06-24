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
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.api.AuthenticatedLobstersApi
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.toError
import dev.msfjarvis.claw.common.NetworkState
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import java.io.IOException
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class TagFilterViewModel(
  private val api: LobstersApi,
  private val authenticatedApi: AuthenticatedLobstersApi,
  private val tagBlockRepository: TagBlockRepository,
  private val sessionCookieStore: SessionCookieStore,
  @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

  val tagBlocks: StateFlow<List<TagBlock>>
    field = MutableStateFlow<List<TagBlock>>(emptyList())

  val filteredTags: StateFlow<ImmutableSet<String>> =
    tagBlocks
      .map { blocks -> blocks.mapTo(mutableSetOf()) { it.tag }.toPersistentSet() }
      .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet<String>().toPersistentSet())

  var allTags by mutableStateOf<NetworkState>(NetworkState.Loading)
    private set

  var isDirty by mutableStateOf(false)
    private set

  var isSaving by mutableStateOf(false)
    private set

  var saveError by mutableStateOf<String?>(null)
    private set

  private var initialBlocks: List<TagBlock> = emptyList()
  private var authenticatedAtLoad: Boolean = false

  init {
    viewModelScope.launch { loadTags() }
  }

  fun saveTagBlock(tag: String, expirationMillis: Long?) {
    setDraft(tagBlocks.value.filterNot { it.tag == tag } + TagBlock(tag, expirationMillis))
  }

  fun removeTagBlock(tag: String) {
    setDraft(tagBlocks.value.filterNot { it.tag == tag })
  }

  fun discardChanges() {
    setDraft(initialBlocks)
    saveError = null
  }

  fun save() {
    if (!isDirty || isSaving) return
    viewModelScope.launch {
      isSaving = true
      saveError = null
      val result = runSuspendCatching { withContext(ioDispatcher) { persistDraft() } }
      result.fold(
        success = {
          isSaving = false
        },
        failure = {
          isSaving = false
          saveError = it.message ?: "Failed to save tag filters"
        },
      )
    }
  }

  fun triggerCleanupNow() {
    viewModelScope.launch { tagBlockRepository.removeExpiredTags() }
  }

  private suspend fun loadTags() {
    allTags = NetworkState.Loading
    allTags =
      runSuspendCatching<ImmutableList<Tag>> { withContext(ioDispatcher) { loadTagsInternal() } }
        .fold(
          success = { details -> NetworkState.Success(details) },
          failure = { NetworkState.Error(error = it, description = "Failed to load tags") },
        )
  }

  private suspend fun loadTagsInternal(): ImmutableList<Tag> {
    val startedAuthenticated = sessionCookieStore.getUsername() != null
    authenticatedAtLoad = startedAuthenticated
    val page =
      when (val result = api.getFilters()) {
        is Success -> result.value
        is Failure.NetworkFailure -> throw result.error
        is Failure.UnknownFailure -> throw result.error
        is Failure.HttpFailure -> throw result.toError()
        is Failure.ApiFailure -> throw IOException("API returned an invalid response")
      }
    val currentBlocks = tagBlockRepository.getTagBlocks().first().normalized()
    val shouldUseRemoteState = startedAuthenticated && sessionCookieStore.getUsername() != null
    val mergedBlocks =
      if (shouldUseRemoteState) {
        val temporaryRows =
          currentBlocks
            .filterNot { it.isPermanent || it.tag in page.blockedTags }
            .sortedBy { it.tag }
        (page.blockedTags.map { TagBlock(it, null) } + temporaryRows).normalized()
      } else {
        authenticatedAtLoad = false
        currentBlocks
      }
    if (shouldUseRemoteState) {
      tagBlockRepository.replaceTagBlocks(mergedBlocks)
    }
    initialBlocks = mergedBlocks
    setDraft(mergedBlocks)
    return page.tags.toImmutableList()
  }

  private suspend fun persistDraft() {
    val draft = tagBlocks.value.normalized()
    if (!authenticatedAtLoad) {
      tagBlockRepository.replaceTagBlocks(draft)
      initialBlocks = draft
      isDirty = false
      return
    }

    val permanentTags = draft.filter { it.isPermanent }.mapTo(mutableSetOf()) { it.tag }
    val temporaryRows = draft.filterNot { it.isPermanent }
    val remoteCanonical =
      when (val result = authenticatedApi.saveBlockedTags(permanentTags)) {
        is Success -> result.value
        is Failure.NetworkFailure -> throw result.error
        is Failure.UnknownFailure -> throw result.error
        is Failure.HttpFailure -> throw result.toError()
        is Failure.ApiFailure -> throw IOException("API returned an invalid response")
      }
    val finalBlocks =
      (remoteCanonical.map { TagBlock(it, null) } +
          temporaryRows.filterNot { it.tag in remoteCanonical })
        .normalized()
    tagBlockRepository.replaceTagBlocks(finalBlocks)
    initialBlocks = finalBlocks
    setDraft(initialBlocks)
  }

  private fun setDraft(blocks: List<TagBlock>) {
    val normalized = blocks.normalized()
    tagBlocks.value = normalized
    isDirty = normalized != initialBlocks
  }

  private fun List<TagBlock>.normalized(): List<TagBlock> = distinctBy {
    it.tag
  }
    .sortedBy { it.tag }
}
