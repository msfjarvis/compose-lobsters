package dev.msfjarvis.lobsters

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.api.LobstersApi
import dev.msfjarvis.lobsters.compose.utils.IconResource
import dev.msfjarvis.lobsters.data.LobstersViewModel
import dev.msfjarvis.lobsters.ui.LobstersItem
import dev.msfjarvis.lobsters.ui.LobstersTheme
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher
import javax.inject.Inject

val UrlLauncherAmbient = ambientOf<UrlLauncher> { error("Needs to be provided") }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var apiClient: LobstersApi
  private val viewModel: LobstersViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Providers(UrlLauncherAmbient provides urlLauncher) {
        LobstersTheme {
          LobstersApp(viewModel)
        }
      }
    }
  }
}

@Composable
fun LobstersApp(
  viewModel: LobstersViewModel
) {
  val urlLauncher = UrlLauncherAmbient.current
  val state = viewModel.posts.collectAsState()
  val lastIndex = state.value.lastIndex

  Scaffold(
    topBar = { TopAppBar({ Text(text = stringResource(R.string.app_name)) }) },
    bodyContent = {
      if (state.value.isEmpty()) {
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          IconResource(R.drawable.ic_sync_problem_24px)
          Text(stringResource(R.string.nothing_to_see_here))
        }
      } else {
        LazyColumnForIndexed(state.value) { index, item ->
          if (lastIndex == index) {
            viewModel.getMorePosts()
          }
          LobstersItem(
            item,
            linkOpenAction = { post -> urlLauncher.launch(post.url) },
            commentOpenAction = { post -> urlLauncher.launch(post.commentsUrl) },
          )
        }
      }
    }
  )
}
