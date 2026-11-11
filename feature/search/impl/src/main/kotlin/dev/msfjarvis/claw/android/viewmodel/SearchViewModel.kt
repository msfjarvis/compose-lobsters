/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.PAGE_SIZE
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.STARTING_PAGE_INDEX
import dev.msfjarvis.claw.feature.search.api.SearchUiState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MILLIS = 300L

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class SearchViewModel(private val searchPagingSourceFactory: SearchPagingSource.Factory) :
  ViewModel() {
  val uiState: StateFlow<SearchUiState>
    field: MutableStateFlow<SearchUiState> = MutableStateFlow(SearchUiState())
  private var stateRestored = false

  fun restoreUiState(state: SearchUiState) {
    if (!stateRestored) {
      uiState.value = state
      stateRestored = true
      if (state.isActive) scheduleSearch()
    }
  }

  val searchResults =
    uiState
      .map { it.lastExecutedQuery to it.requestGeneration }
      .distinctUntilChanged()
      .flatMapLatest { (query, _) ->
        Pager(
            PagingConfig(pageSize = PAGE_SIZE),
            initialKey = STARTING_PAGE_INDEX,
            pagingSourceFactory = { searchPagingSourceFactory.create { query.orEmpty() } },
          )
          .flow
      }
      .cachedIn(viewModelScope)

  private var searchJob: Job? = null

  fun startSearch() {
    stateRestored = true
    uiState.value = uiState.value.startSearch()
    scheduleSearch()
  }

  fun updateQuery(query: String) {
    stateRestored = true
    uiState.value = uiState.value.updateQuery(query)
    scheduleSearch()
  }

  fun executeSearch(query: String) {
    stateRestored = true
    uiState.value = uiState.value.executeSearch(query.trim())
  }

  fun dismissSearch() {
    stateRestored = true
    searchJob?.cancel()
    uiState.value = uiState.value.dismissSearch()
  }

  private fun scheduleSearch() {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(SEARCH_DEBOUNCE_MILLIS.milliseconds)
      val state = uiState.value
      if (state.isActive && state.query.trim() != state.lastExecutedQuery) {
        executeSearch(state.query)
      }
    }
  }
}
