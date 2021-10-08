package dev.msfjarvis.claw.common.paging

import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kuuurt.paging.multiplatform.Pager
import com.kuuurt.paging.multiplatform.PagingResult
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class Paging(
  private val coroutineScope: CoroutineScope,
  private val getMorePosts: suspend (Int) -> List<LobstersPost>,
) {
  private val pager =
    Pager(
      clientScope = coroutineScope,
      config = PagingConfig(20),
      initialKey = 1,
      getItems = { currentKey, _ ->
        val items = getMorePosts(currentKey)
        PagingResult(
          items = items,
          currentKey = currentKey,
          prevKey = { if (currentKey == 1) null else currentKey - 1 },
          nextKey = { currentKey + 1 },
        )
      }
    )
  val pagingData
    get() = pager.pagingData.cachedIn(coroutineScope)
}
