package dev.msfjarvis.claw.common.paging

import dev.msfjarvis.claw.model.LobstersPost

sealed class LobstersPagingResult {
  class Success(val list: List<LobstersPost>) : LobstersPagingResult()
  class Error(val error: Throwable) : LobstersPagingResult()
  object Loading : LobstersPagingResult()
}
