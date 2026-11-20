/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.paging.SearchPagingSource
import dev.msfjarvis.claw.android.ui.lists.SearchResultsList
import dev.msfjarvis.claw.android.viewmodel.SearchViewModel
import dev.msfjarvis.claw.api.LobstersSearchApi
import dev.msfjarvis.claw.feature.search.api.SearchUiState
import dev.msfjarvis.claw.model.LobstersPost
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchPagingRestorationTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun laterPageAndScrollPositionRestoreIntoNewViewModel() {
    val api =
      object : LobstersSearchApi {
        override suspend fun searchPosts(
          searchQuery: String,
          page: Int,
        ): ApiResult<List<LobstersPost>, Unit> =
          ApiResult.success(
            if (page <= 5)
              List(25) { LobstersPost("$page-$it", title = "Post $page-$it", submitter = "alice") }
            else emptyList()
          )
      }
    val factory =
      object : SearchPagingSource.Factory {
        override fun create(queryProvider: () -> String) =
          SearchPagingSource(api, queryProvider, Dispatchers.IO)
      }
    fun newModel() = SearchViewModel(factory)
    var model = newModel()
    lateinit var listState: LazyListState
    val count = AtomicInteger()
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      listState = rememberLazyListState()
      LaunchedEffect(model) {
        model.restoreUiState(
          SearchUiState(
            isActive = true,
            query = "query",
            lastExecutedQuery = "query",
            requestGeneration = 1,
          ),
          listState.firstVisibleItemIndex,
        )
      }
      val items = model.searchResults.collectAsLazyPagingItems()
      count.set(items.itemCount)
      SearchResultsList(lazyPagingItems = items, onClearanceCookie = {}) { modifier ->
        LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
          items(count = items.itemCount) { index ->
            Text(items[index]?.title.orEmpty(), modifier = Modifier.height(80.dp))
          }
        }
      }
    }
    compose.waitUntil(10_000) { count.get() >= 25 }
    // Load enough pages before saving the later anchor.
    compose.runOnIdle { runBlocking { listState.scrollToItem(24) } }
    compose.waitUntil(10_000) { count.get() >= 50 }
    compose.runOnIdle { runBlocking { listState.scrollToItem(49) } }
    compose.waitUntil(10_000) { count.get() >= 75 }
    compose.runOnIdle { runBlocking { listState.scrollToItem(55, 3) } }
    compose.runOnIdle {
      assertEquals(55, listState.firstVisibleItemIndex)
      model = newModel()
    }
    restoration.emulateSavedInstanceStateRestore()
    compose.waitUntil(10_000) { count.get() >= 75 }
    compose.runOnIdle {
      assertEquals(55, listState.firstVisibleItemIndex)
      assertEquals(3, listState.firstVisibleItemScrollOffset)
    }
  }

  @Test fun restoredAnchorClampsWhenSavedPageIsShortened() = assertRestoredAnchorClamps(5)

  @Test fun restoredAnchorClampsWhenSavedPageDisappears() = assertRestoredAnchorClamps(0)

  private fun assertRestoredAnchorClamps(restoredPageSize: Int) {
    val pageThreeItemCount = AtomicInteger(25)
    val restoredPageLoads = AtomicInteger()
    val api =
      object : LobstersSearchApi {
        override suspend fun searchPosts(
          searchQuery: String,
          page: Int,
        ): ApiResult<List<LobstersPost>, Unit> =
          ApiResult.success(
            when (page) {
              1,
              2 ->
                List(25) {
                  LobstersPost("$page-$it", title = "Post $page-$it", submitter = "alice")
                }
              3 -> {
                val size = pageThreeItemCount.get()
                if (size != 25) restoredPageLoads.incrementAndGet()
                List(size) { LobstersPost("3-$it", title = "Post 3-$it", submitter = "alice") }
              }
              else -> emptyList()
            }
          )
      }
    val factory =
      object : SearchPagingSource.Factory {
        override fun create(queryProvider: () -> String) =
          SearchPagingSource(api, queryProvider, Dispatchers.IO)
      }
    fun newModel() = SearchViewModel(factory)
    var model = newModel()
    lateinit var listState: LazyListState
    val count = AtomicInteger()
    val restoration = StateRestorationTester(compose)
    restoration.setContent {
      listState = rememberLazyListState()
      LaunchedEffect(model) {
        model.restoreUiState(
          SearchUiState(
            isActive = true,
            query = "query",
            lastExecutedQuery = "query",
            requestGeneration = 1,
          ),
          listState.firstVisibleItemIndex,
        )
      }
      val items = model.searchResults.collectAsLazyPagingItems()
      count.set(items.itemCount)
      SearchResultsList(lazyPagingItems = items, onClearanceCookie = {}) { modifier ->
        LazyColumn(modifier = modifier.fillMaxSize(), state = listState) {
          items(count = items.itemCount) { index ->
            Text(items[index]?.title.orEmpty(), modifier = Modifier.height(80.dp))
          }
        }
      }
    }

    compose.waitUntil(10_000) { count.get() >= 25 }
    compose.runOnIdle { runBlocking { listState.scrollToItem(24) } }
    compose.waitUntil(10_000) { count.get() >= 50 }
    compose.runOnIdle { runBlocking { listState.scrollToItem(49) } }
    compose.waitUntil(10_000) { count.get() >= 75 }
    compose.runOnIdle { runBlocking { listState.scrollToItem(55, 3) } }
    compose.runOnIdle {
      assertEquals(55, listState.firstVisibleItemIndex)
      pageThreeItemCount.set(restoredPageSize)
      model = newModel()
    }
    restoration.emulateSavedInstanceStateRestore()
    compose.waitUntil(10_000) {
      restoredPageLoads.get() > 0 && count.get() == 50 + restoredPageSize
    }
    val lastTitle = if (restoredPageSize == 0) "Post 2-24" else "Post 3-${restoredPageSize - 1}"
    compose.waitUntil(10_000) {
      compose.onAllNodes(hasText(lastTitle)).fetchSemanticsNodes().isNotEmpty()
    }
    compose.runOnIdle {
      // The last available result should remain visible when the old anchor no longer exists.
      val lastIndex = 49 + restoredPageSize
      assertEquals(lastIndex, listState.layoutInfo.visibleItemsInfo.last().index)
      org.junit.Assert.assertTrue(listState.firstVisibleItemIndex <= lastIndex)
    }
  }
}
