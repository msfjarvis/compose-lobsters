/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.feature.search.api.SearchUiState
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchRestorationTest {
  private val dispatcher = StandardTestDispatcher()

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `refresh keys use one-based pages`() {
    val api =
      object : LobstersSearchApi {
        override suspend fun searchPosts(
          searchQuery: String,
          page: Int,
        ): ApiResult<List<LobstersPost>, Unit> = ApiResult.success(emptyList())
      }
    val source = SearchPagingSource(api, { "query" }, dispatcher)
    fun state(anchor: Int) =
      PagingState<Int, dev.msfjarvis.claw.model.UIPost>(
        pages = emptyList(),
        anchorPosition = anchor,
        config = PagingConfig(pageSize = 25),
        leadingPlaceholderCount = 0,
      )
    assertThat(source.getRefreshKey(state(0))).isEqualTo(1)
    assertThat(source.getRefreshKey(state(25))).isEqualTo(2)
    assertThat(source.getRefreshKey(state(55))).isEqualTo(3)
  }

  @Test
  fun `restored search loads the anchor page and new searches start at page one`() =
    runTest(dispatcher) {
      val pages = mutableListOf<Pair<String, Int>>()
      val api =
        object : LobstersSearchApi {
          override suspend fun searchPosts(
            searchQuery: String,
            page: Int,
          ): ApiResult<List<LobstersPost>, Unit> {
            pages += searchQuery to page
            return ApiResult.success(
              if (page <= 4) {
                List(25) { index ->
                  LobstersPost(
                    shortId = "$page-$index",
                    title = "Post $page-$index",
                    submitter = "alice",
                  )
                }
              } else {
                emptyList()
              }
            )
          }
        }
      val factory =
        object : SearchPagingSource.Factory {
          override fun create(queryProvider: () -> String) =
            SearchPagingSource(api, queryProvider, dispatcher)
        }
      val model = SearchViewModel(factory)
      model.restoreUiState(
        SearchUiState(
          isActive = true,
          query = "kotlin",
          lastExecutedQuery = "kotlin",
          requestGeneration = 1,
        ),
        firstVisibleItemIndex = 55,
      )
      val restored = model.searchResults.asSnapshot { scrollTo(55) }
      assertThat(pages.first()).isEqualTo("kotlin" to 3)
      assertThat(restored.map { it.shortId }).contains("3-5")
      pages.clear()
      model.executeSearch("compose")
      advanceUntilIdle()
      model.searchResults.asSnapshot()
      assertThat(pages.first()).isEqualTo("compose" to 1)
    }
}
