package dev.msfjarvis.lobsters.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.lobsters.ui.comments.CommentsPage
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.posts.NetworkPosts
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.settings.SettingsActivity
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel
import dev.msfjarvis.lobsters.util.IconResource
import dev.msfjarvis.lobsters.utils.get
import kotlinx.coroutines.launch

@Composable
fun LobstersApp() {
  val viewModel: LobstersViewModel = viewModel()
  val context = LocalContext.current
  val scaffoldState = rememberScaffoldState()
  val navController = rememberNavController()
  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()

  val savedPosts by viewModel.savedPosts.collectAsState()
  val hottestPostsListState = rememberLazyListState()
  val newestPostsListState = rememberLazyListState()

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: Destination.startDestination.route
  val currentDestination = Destination.getDestinationFromRoute(currentRoute)
  val navigateToDestination: (destination: Destination) -> Unit = { destination ->
    navController.navigate(destination.route) { launchSingleTop = true }
  }
  val jumpToIndex: suspend (Int, Destination) -> Unit = { index, screen ->
    when (screen) {
      Destination.Hottest -> {
        if (hottestPosts.loadState.refresh != LoadState.Loading) {
          hottestPostsListState.animateScrollToItem(index)
        }
      }
      Destination.Newest -> {
        if (newestPosts.loadState.refresh != LoadState.Loading) {
          newestPostsListState.animateScrollToItem(index)
        }
      }
      else -> {}
    }
  }

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      LobstersTopAppBar(
        currentDestination,
        viewModel::toggleSortOrder,
      ) { context.startActivity(Intent(context, SettingsActivity::class.java)) }
    },
    bottomBar = {
      LobstersBottomNav(
        currentDestination,
        navigateToDestination,
        jumpToIndex,
      )
    },
  ) { innerPadding ->
    NavHost(navController, startDestination = Destination.startDestination.route) {
      val uri = "https://lobste.rs"

      composable(
        route = Destination.Hottest.route,
        deepLinks = listOf(navDeepLink { uriPattern = uri }, navDeepLink { uriPattern = "$uri/" })
      ) {
        NetworkPosts(
          posts = hottestPosts,
          listState = hottestPostsListState,
          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
          isPostSaved = viewModel::isPostSaved,
          saveAction = viewModel::toggleSave,
          viewComments = { navController.navigate("comments/$it") },
        )
      }
      composable(
        route = Destination.Newest.route,
        deepLinks =
          listOf(
            navDeepLink { uriPattern = "$uri/recent" },
            navDeepLink { uriPattern = "$uri/recent/" }
          )
      ) {
        NetworkPosts(
          posts = newestPosts,
          listState = newestPostsListState,
          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
          isPostSaved = viewModel::isPostSaved,
          saveAction = viewModel::toggleSave,
          viewComments = { navController.navigate("comments/$it") },
        )
      }
      composable(Destination.Saved.route) {
        SavedPosts(
          posts = savedPosts,
          saveAction = viewModel::toggleSave,
          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
          sortReversed = viewModel.getSortOrder(),
          viewComments = { navController.navigate("comments/$it") },
        )
      }
      composable(
        route = Destination.Comments.route,
        arguments = listOf(navArgument("postId") { type = NavType.StringType }),
        deepLinks =
          listOf(
            navDeepLink { uriPattern = "$uri/s/{postId}/.*" },
            navDeepLink { uriPattern = "$uri/s/{postId}" }
          )
      ) { backStackEntry ->
        CommentsPage(
          postId = requireNotNull(backStackEntry.arguments?.getString("postId")),
          paddingValues = innerPadding,
          scaffoldState = scaffoldState,
          getDetails = viewModel::getPostDetails,
        )
      }
    }
  }
}

@Composable
fun LobstersBottomNav(
  currentDestination: Destination,
  navigateToDestination: (destination: Destination) -> Unit,
  jumpToIndex: suspend (index: Int, screen: Destination) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  BottomNavigation(modifier = Modifier.testTag("LobstersBottomNav")) {
    Destination.values().filter { it.bottombar }.forEach { screen ->
      BottomNavigationItem(
        icon = {
          IconResource(
            resourceId = screen.badgeRes,
            contentDescription = screen.labelRes.get(),
          )
        },
        label = { Text(screen.labelRes.get()) },
        selected = currentDestination == screen,
        modifier = Modifier.testTag(screen.name),
        alwaysShowLabel = false,
        onClick = {
          if (screen != currentDestination) {
            navigateToDestination(screen)
          } else if (screen.route != Destination.Saved.route) {
            coroutineScope.launch { jumpToIndex(0, screen) }
          }
        }
      )
    }
  }
}
