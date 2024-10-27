/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.TwoPaneLayoutPostActions
import dev.msfjarvis.claw.android.ui.lists.NetworkPostsForTwoPaneLayout
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TwoPaneLayout(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {

  val context = LocalContext.current
  val hottestListState = rememberLazyListState()
  val navController = rememberNavController()

  // Track the selected postId as a mutable state
  var selectedPostId by remember { mutableStateOf<String?>(null) }

  // Initialize postActions with selectedPostId as a mutable state
  val postActions = remember {
    TwoPaneLayoutPostActions(context, urlLauncher, viewModel, { selectedPostId = it })
  }

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()

  // Navigator state
  val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "The app icon for Claw",
            modifier = Modifier.size(48.dp),
          )
        },
        title = { Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
      )
    },
    content = { paddingValues ->
      NavigableListDetailPaneScaffold(
        modifier = modifier.padding(paddingValues),
        navigator = navigator,
        listPane = {
          NetworkPostsForTwoPaneLayout(
            lazyPagingItems = hottestPosts,
            listState = hottestListState,
            postActions = postActions,
            contentPadding = PaddingValues(),
            modifier = Modifier.fillMaxSize(),
          ) { postId ->
            selectedPostId = postId // Update selectedPostId on click
          }
        },
        detailPane = {
          selectedPostId?.let { postId ->
            CommentsPage(
              postId = postId,
              postActions = postActions,
              htmlConverter = htmlConverter,
              getSeenComments = viewModel::getSeenComments,
              markSeenComments = viewModel::markSeenComments,
              openUserProfile = { navController.navigate(User(it)) },
              contentPadding = PaddingValues(),
              modifier = Modifier.fillMaxSize(),
            )
          }
            ?: Box(Modifier.fillMaxSize()) {
              // Placeholder when no post is selected
              Text(
                text = "Select a post to view comments",
                modifier = Modifier.align(Alignment.Center),
              )
            }
        },
      )
    },
  )
}
