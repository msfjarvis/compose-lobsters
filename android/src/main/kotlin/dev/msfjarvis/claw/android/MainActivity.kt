package dev.msfjarvis.claw.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.paging.Pager
import androidx.paging.PagingConfig
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.ui.LobstersApp
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject lateinit var api: LobstersApi

  @Inject lateinit var urlLauncher: UrlLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val pager = Pager(PagingConfig(20)) { LobstersPagingSource(api::getHottestPosts) }
    setContent {
      LobstersApp(
        pager,
        urlLauncher,
      )
    }
  }
}
