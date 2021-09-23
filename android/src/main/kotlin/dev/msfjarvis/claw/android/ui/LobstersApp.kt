package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.msfjarvis.claw.api.model.LobstersPost
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@Composable
fun LobstersApp(
  pager: Pager<Int, LobstersPost>,
  urlLauncher: UrlLauncher,
) {
  val systemUiController = rememberSystemUiController()
  val scaffoldState = rememberScaffoldState()
  LobstersTheme(darkTheme = isSystemInDarkTheme()) {
    ProvideWindowInsets {
      val useDarkIcons = MaterialTheme.colors.isLight
      val systemBarsColor = MaterialTheme.colors.primarySurface

      SideEffect {
        systemUiController.setSystemBarsColor(color = systemBarsColor, darkIcons = useDarkIcons)
      }
      val items = pager.flow.collectAsLazyPagingItems()
      Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ClawAppBar(modifier = Modifier.statusBarsPadding()) },
        modifier = Modifier,
      ) {
        if (items.loadState.refresh != LoadState.Loading) {
          NetworkPosts(
            items = items,
            urlLauncher = urlLauncher,
            modifier = Modifier.padding(top = 16.dp),
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
