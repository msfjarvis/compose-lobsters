/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.search.api

data class SearchUiState(
  val isActive: Boolean = false,
  val query: String = "",
  val lastExecutedQuery: String? = null,
  val requestGeneration: Long = 0,
) {
  fun startSearch() = copy(isActive = true)

  fun updateQuery(query: String) = copy(query = query)

  fun executeSearch(query: String) =
    copy(query = query, lastExecutedQuery = query, requestGeneration = requestGeneration + 1)

  fun dismissSearch() = SearchUiState(lastExecutedQuery = "")
}
