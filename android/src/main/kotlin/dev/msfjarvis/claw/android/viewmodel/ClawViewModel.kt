package dev.msfjarvis.claw.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.api.LobstersApi
import javax.inject.Inject

@HiltViewModel
class ClawViewModel @Inject constructor(
  api: LobstersApi,
) : ViewModel() {
  private val pager = Pager(PagingConfig(20)) { LobstersPagingSource(api::getHottestPosts) }

  val pagerFlow
    get() = pager.flow
}
