/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.paging.CombinedLoadStates
import androidx.paging.DifferCallback
import androidx.paging.ItemSnapshotList
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.NullPaddedList
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <T : Any> Flow<PagingData<T>>.collectAsLazyPagingItems(): LazyPagingItems<T> {
  val lazyPagingItems = remember(this) { LazyPagingItems(this) }

  LaunchedEffect(lazyPagingItems) { lazyPagingItems.collectPagingData() }
  LaunchedEffect(lazyPagingItems) { lazyPagingItems.collectLoadState() }

  return lazyPagingItems
}

fun <T : Any> LazyListScope.items(
  items: LazyPagingItems<T>,
  key: ((item: T) -> Any)? = null,
  itemContent: @Composable LazyItemScope.(value: T?) -> Unit
) {
  items(
    count = items.itemCount,
    key =
      if (key == null) null
      else
        { index ->
          val item = items.peek(index)
          if (item == null) {
            PagingPlaceholderKey(index)
          } else {
            key(item)
          }
        }
  ) { index -> itemContent(items[index]) }
}

class LazyPagingItems<T : Any>
internal constructor(
  /** the [Flow] object which contains a stream of [PagingData] elements. */
  private val flow: Flow<PagingData<T>>
) {
  private val mainDispatcher = Dispatchers.Main

  /** Contains the latest items list snapshot collected from the [flow]. */
  private var itemSnapshotList by mutableStateOf(ItemSnapshotList<T>(0, 0, emptyList()))

  /** The number of items which can be accessed. */
  val itemCount: Int
    get() = itemSnapshotList.size

  private val differCallback: DifferCallback =
    object : DifferCallback {
      override fun onChanged(position: Int, count: Int) {
        if (count > 0) {
          updateItemSnapshotList()
        }
      }

      override fun onInserted(position: Int, count: Int) {
        if (count > 0) {
          updateItemSnapshotList()
        }
      }

      override fun onRemoved(position: Int, count: Int) {
        if (count > 0) {
          updateItemSnapshotList()
        }
      }
    }

  private val pagingDataDiffer =
    object : PagingDataDiffer<T>(differCallback = differCallback, mainDispatcher = mainDispatcher) {
      override suspend fun presentNewList(
        previousList: NullPaddedList<T>,
        newList: NullPaddedList<T>,
        newCombinedLoadStates: CombinedLoadStates,
        lastAccessedIndex: Int,
        onListPresentable: () -> Unit
      ): Int? {
        onListPresentable()
        updateItemSnapshotList()
        return null
      }
    }

  private fun updateItemSnapshotList() {
    itemSnapshotList = pagingDataDiffer.snapshot()
  }

  /**
   * Returns the presented item at the specified position, notifying Paging of the item access to
   * trigger any loads necessary to fulfill prefetchDistance.
   *
   * @see peek
   */
  operator fun get(index: Int): T? {
    pagingDataDiffer[index] // this registers the value load
    return itemSnapshotList[index]
  }

  /**
   * Returns the state containing the item specified at [index] and notifies Paging of the item
   * accessed in order to trigger any loads necessary to fulfill [PagingConfig.prefetchDistance].
   *
   * @param index the index of the item which should be returned.
   * @return the state containing the item specified at [index] or null if the item is a placeholder
   * or [index] is not within the correct bounds.
   */
  @Composable
  @Deprecated(
    "Use get() instead. It will return you the value not wrapped into a State",
    ReplaceWith("this[index]")
  )
  fun getAsState(index: Int): State<T?> {
    return rememberUpdatedState(get(index))
  }

  /**
   * Returns the presented item at the specified position, without notifying Paging of the item
   * access that would normally trigger page loads.
   *
   * @param index Index of the presented item to return, including placeholders.
   * @return The presented item at position [index], `null` if it is a placeholder
   */
  fun peek(index: Int): T? {
    return itemSnapshotList[index]
  }

  /**
   * Returns a new [ItemSnapshotList] representing the currently presented items, including any
   * placeholders if they are enabled.
   */
  fun snapshot(): ItemSnapshotList<T> {
    return itemSnapshotList
  }

  /**
   * Retry any failed load requests that would result in a [LoadState.Error] update to this
   * [LazyPagingItems].
   *
   * Unlike [refresh], this does not invalidate [PagingSource], it only retries failed loads within
   * the same generation of [PagingData].
   *
   * [LoadState.Error] can be generated from two types of load requests:
   * * [PagingSource.load] returning [PagingSource.LoadResult.Error]
   * * [RemoteMediator.load] returning [RemoteMediator.MediatorResult.Error]
   */
  fun retry() {
    pagingDataDiffer.retry()
  }

  /**
   * Refresh the data presented by this [LazyPagingItems].
   *
   * [refresh] triggers the creation of a new [PagingData] with a new instance of [PagingSource] to
   * represent an updated snapshot of the backing dataset. If a [RemoteMediator] is set, calling
   * [refresh] will also trigger a call to [RemoteMediator.load] with [LoadType] [REFRESH] to allow
   * [RemoteMediator] to check for updates to the dataset backing [PagingSource].
   *
   * Note: This API is intended for UI-driven refresh signals, such as swipe-to-refresh.
   * Invalidation due repository-layer signals, such as DB-updates, should instead use
   * [PagingSource.invalidate].
   *
   * @see PagingSource.invalidate
   */
  fun refresh() {
    pagingDataDiffer.refresh()
  }

  /** A [CombinedLoadStates] object which represents the current loading state. */
  public var loadState: CombinedLoadStates by
    mutableStateOf(
      CombinedLoadStates(
        refresh = InitialLoadStates.refresh,
        prepend = InitialLoadStates.prepend,
        append = InitialLoadStates.append,
        source = InitialLoadStates
      )
    )
    private set

  internal suspend fun collectLoadState() {
    pagingDataDiffer.loadStateFlow.collect { loadState = it }
  }

  internal suspend fun collectPagingData() {
    flow.collectLatest { pagingDataDiffer.collectFrom(it) }
  }
}

private val IncompleteLoadState = LoadState.NotLoading(false)
private val InitialLoadStates =
  LoadStates(IncompleteLoadState, IncompleteLoadState, IncompleteLoadState)

data class PagingPlaceholderKey(private val index: Int)
