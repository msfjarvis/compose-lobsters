package dev.msfjarvis.claw.common.paging

import dev.icerock.moko.paging.LambdaPagedListDataSource
import dev.icerock.moko.paging.Pagination
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LobstersPagination(
  coroutineScope: CoroutineScope,
  initialList: List<LobstersPost> = emptyList(),
  load: suspend (List<LobstersPost>) -> List<LobstersPost>,
) {

  private val _pagingResultFlow: MutableSharedFlow<LobstersPagingResult> =
    MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
  val pagingResultFlow = _pagingResultFlow.asSharedFlow()

  private val pagination =
    Pagination(
      parentScope = coroutineScope,
      dataSource = LambdaPagedListDataSource { currentList -> load(currentList ?: emptyList()) },
      comparator = { first, second -> first.shortId.compareTo(second.shortId) },
      nextPageListener = {},
      refreshListener = {},
      initValue = initialList
    )

  init {
    init()
  }

  private fun init() {
    pagination.state.addObserver { resourceState ->
      if (resourceState.isEmpty() || resourceState.isFailed()) {
        val error = resourceState.errorValue() ?: Throwable("resourceState is empty")
        _pagingResultFlow.tryEmit(LobstersPagingResult.Error(error))
        return@addObserver
      }

      if (resourceState.isSuccess()) {
        val posts = resourceState.dataValue() ?: emptyList()
        _pagingResultFlow.tryEmit(LobstersPagingResult.Success(posts))
        return@addObserver
      }
    }
  }

  fun loadFirstPage() {
    _pagingResultFlow.tryEmit(LobstersPagingResult.Loading)
    pagination.loadFirstPage()
  }

  fun loadNextPage() {
    _pagingResultFlow.tryEmit(LobstersPagingResult.Loading)
    pagination.loadNextPage()
  }

  fun refresh() {
    _pagingResultFlow.tryEmit(LobstersPagingResult.Loading)
    pagination.refresh()
  }
}
