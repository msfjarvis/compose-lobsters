package dev.msfjarvis.claw.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.ui.NetworkPosts
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var api: LobstersApi
  @Inject lateinit var urlLauncher: UrlLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val pager = Pager(PagingConfig(20)) { LobstersPagingSource(api::getHottestPosts) }
    setContent {
      LobstersTheme(darkTheme = isSystemInDarkTheme()) {
        val items = pager.flow.collectAsLazyPagingItems()
        NetworkPosts(
          items = items,
          urlLauncher = urlLauncher,
        )
      }
    }
  }
}
