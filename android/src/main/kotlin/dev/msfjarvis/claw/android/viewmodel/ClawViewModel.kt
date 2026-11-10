/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.PAGE_SIZE
import dev.msfjarvis.claw.feature.posts.api.PostPagingConfig.STARTING_PAGE_INDEX
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@Inject
@ViewModelKey
@ContributesIntoMap(scope = AppScope::class, binding = binding<ViewModel>())
class ClawViewModel(private val searchPagingSourceFactory: SearchPagingSource.Factory) :
  ViewModel() {
  val searchResults =
    Pager(
        PagingConfig(pageSize = PAGE_SIZE),
        initialKey = STARTING_PAGE_INDEX,
        pagingSourceFactory = { searchPagingSourceFactory.create { searchQuery } },
      )
      .flow
  var searchQuery by mutableStateOf("")
}
