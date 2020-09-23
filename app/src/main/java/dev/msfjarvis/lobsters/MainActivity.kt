package dev.msfjarvis.lobsters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.api.LobstersApi
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.LobstersItem
import dev.msfjarvis.lobsters.ui.LobstersTheme
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

val UrlLauncherAmbient = ambientOf<UrlLauncher> { error("Needs to be provided") }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher
  @Inject lateinit var apiClient: LobstersApi

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Providers(UrlLauncherAmbient provides urlLauncher) {
        LobstersTheme {
          val posts = mutableStateListOf<LobstersPost>()
          lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
              posts.addAll(apiClient.getHottestPosts(1))
            }
          }
          LobstersApp(posts)
        }
      }
    }
  }
}

@Composable
fun LobstersApp(
  items: List<LobstersPost>,
) {
  val urlLauncher = UrlLauncherAmbient.current

  Scaffold(
    topBar = { TopAppBar({ Text(text = stringResource(R.string.app_name)) }) },
    bodyContent = {
      LazyColumnFor(items) { item ->
        LobstersItem(item) { post ->
          urlLauncher.launch(post.url)
        }
      }
    }
  )
}
