package dev.msfjarvis.lobsters

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animate
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Providers
import androidx.compose.runtime.State
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.compose.utils.IconResource
import dev.msfjarvis.lobsters.data.LobstersViewModel
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.LobstersItem
import dev.msfjarvis.lobsters.ui.LobstersTheme
import dev.msfjarvis.lobsters.ui.savedTitleColor
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher
import javax.inject.Inject

val UrlLauncherAmbient = ambientOf<UrlLauncher> { error("Needs to be provided") }

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher
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
  val hottestPostsState = viewModel.posts.collectAsState()
  val savedPostsState = viewModel.savedPosts.collectAsState()
  val lastIndex = hottestPostsState.value.lastIndex
  val showSaved = remember { mutableStateOf(false) }

  Scaffold(
    topBar = { LobsterTopAppbar(showSaved) },
    bodyContent = {
      if ((!showSaved.value && hottestPostsState.value.isEmpty()) || (showSaved.value && savedPostsState.value.isEmpty())) {
        LobsterEmptyList(showSaved)
      } else {
        LobsterList(
          showSaved,
          savedPostsState,
          hottestPostsState,
          lastIndex,
          viewModel,
          urlLauncher
        )
      }
    },
    floatingActionButton = { LobsterFAB(showSaved, viewModel) },
  )
}

@Composable
private fun LobsterFAB(
  showSaved: MutableState<Boolean>,
  viewModel: LobstersViewModel
) {
  val animatedYOffset = animate(if (showSaved.value) 100.dp else 0.dp)

  FloatingActionButton(
    onClick = { viewModel.refreshPosts() },
    modifier = Modifier.offset(y = animatedYOffset)
  ) {
    IconResource(resourceId = R.drawable.ic_refresh_24px)
  }
}

@Composable
private fun LobsterList(
  showSaved: MutableState<Boolean>,
  savedPostsState: State<List<LobstersPost>>,
  hottestPostsState: State<List<LobstersPost>>,
  lastIndex: Int,
  viewModel: LobstersViewModel,
  urlLauncher: UrlLauncher
) {
  val hottestPostsListState = rememberLazyListState()
  val savedPostsListState = rememberLazyListState()

  LazyColumnForIndexed(
    items = if (showSaved.value) savedPostsState.value else hottestPostsState.value,
    state = if (showSaved.value) savedPostsListState else hottestPostsListState,
    modifier = Modifier.padding(horizontal = 8.dp)
  ) { index, item ->
    if (lastIndex == index && !showSaved.value) {
      viewModel.getMorePosts()
    }
    LobstersItem(
      item,
      linkOpenAction = { post -> urlLauncher.launch(post.url.ifEmpty { post.commentsUrl }) },
      commentOpenAction = { post -> urlLauncher.launch(post.commentsUrl) },
      saveAction = { post -> if (!showSaved.value) viewModel.savePost(post) },
    )
  }
}

@Composable
private fun LobsterEmptyList(showSaved: MutableState<Boolean>) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    if (!showSaved.value) {
      IconResource(R.drawable.ic_sync_problem_24px, modifier = Modifier.padding(16.dp))
      Text(stringResource(R.string.loading))
    } else {
      Icon(Icons.Default.FavoriteBorder, tint = savedTitleColor, modifier = Modifier.padding(16.dp))
      Text(stringResource(R.string.no_saved_posts))
    }
  }
}

@Composable
private fun LobsterTopAppbar(showSaved: MutableState<Boolean>) {
  TopAppBar {
    Box(modifier = Modifier.fillMaxWidth()) {
      Text(
        text = if (showSaved.value) "Saved" else "Home",
        modifier = Modifier.padding(16.dp).align(Alignment.CenterStart),
        style = MaterialTheme.typography.h6,
      )
      IconToggleButton(
        checked = showSaved.value,
        onCheckedChange = { showSaved.value = !showSaved.value },
        modifier = Modifier.padding(8.dp).align(Alignment.CenterEnd),
      ) {
        Icon(
          asset = if (showSaved.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          tint = savedTitleColor,
        )
      }
    }
  }
}
