package dev.msfjarvis.claw.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.claw.android.paging.LobstersPagingSource
import dev.msfjarvis.claw.android.ui.ClawAppBar
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
      val scaffoldState = rememberScaffoldState()
      LobstersTheme(darkTheme = isSystemInDarkTheme()) {
        val items = pager.flow.collectAsLazyPagingItems()
        Scaffold(
          scaffoldState = scaffoldState,
          topBar = { ClawAppBar() },
          modifier = Modifier,
        ) { padding ->
          if (items.loadState.refresh != LoadState.Loading) {
            NetworkPosts(
              items = items,
              urlLauncher = urlLauncher,
              modifier = Modifier.padding(padding),
            )
          } else {
            Box(
              modifier = Modifier.fillMaxSize(),
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(64.dp).align(Alignment.Center),
                color = MaterialTheme.colors.secondary,
              )
            }
          }
        }
      }
    }
  }
}
