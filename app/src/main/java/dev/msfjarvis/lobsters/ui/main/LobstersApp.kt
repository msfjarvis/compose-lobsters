package dev.msfjarvis.lobsters.ui.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropScaffoldDefaults
import androidx.compose.material.BackdropValue
import androidx.compose.material.Text
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.ui.posts.HottestPosts
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel

@Composable
fun LobstersApp() {
  val viewModel: LobstersViewModel = viewModel()
  val scaffoldState = rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
  BackdropScaffold(
    scaffoldState = scaffoldState,
    gesturesEnabled = false,
    appBar = {
      Spacer(modifier = Modifier.height(BackdropScaffoldDefaults.PeekHeight.times(0.3f)))
      Text(
        text = stringResource(id = R.string.app_name),
        textAlign = TextAlign.Center,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
      )
      Spacer(modifier = Modifier.height(BackdropScaffoldDefaults.PeekHeight.times(0.3f)))
    },
    backLayerContent = { BackgroundLayerContent(viewModel) },
  ) {
    ForegroundLayerContent(viewModel)
  }
}

@Composable
fun ForegroundLayerContent(
  viewModel: LobstersViewModel,
) {
  val hottestPosts = viewModel.posts.collectAsLazyPagingItems()
  val hottestPostsListState = rememberLazyListState()

  HottestPosts(
    posts = hottestPosts,
    listState = hottestPostsListState,
    isPostSaved = viewModel::isPostSaved,
    saveAction = viewModel::toggleSave,
  )
}

@Composable
fun BackgroundLayerContent(
  viewModel: LobstersViewModel,
) {
  val savedPosts by viewModel.savedPosts.collectAsState()
  SavedPosts(
    posts = savedPosts,
    saveAction = viewModel::toggleSave,
  )
}
