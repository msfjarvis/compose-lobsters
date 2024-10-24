package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.adaptive.*
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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

  val postActions = remember { PostActions(context, urlLauncher, navController, viewModel) }

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()

  // Track the selected postId
  val selectedPostId by remember { mutableStateOf<String?>(null) }

  // Navigator state
  val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

  NavigableListDetailPaneScaffold(
    modifier = modifier,
    navigator = navigator,
    listPane = {
      NetworkPosts(
        lazyPagingItems = hottestPosts,
        listState = hottestListState,
        postActions = postActions,
        contentPadding = PaddingValues(),
        modifier = Modifier.fillMaxSize(),
      )
    },
    detailPane = {
      selectedPostId?.let { postId ->
        CommentsPage(
          postId = postId,
          postActions = postActions,
          htmlConverter = htmlConverter,
          getSeenComments = viewModel::getSeenComments,
          markSeenComments = viewModel::markSeenComments,
          openUserProfile = { navController.navigate(User(it))},
          contentPadding = PaddingValues(),
          modifier = Modifier.fillMaxSize()
        )
      } ?: Box(Modifier.fillMaxSize()) {
        // Placeholder when no post is selected
        Text(
          text = "Select a post to view comments",
          modifier = Modifier.align(Alignment.Center)
        )
      }
    }
  )
}

