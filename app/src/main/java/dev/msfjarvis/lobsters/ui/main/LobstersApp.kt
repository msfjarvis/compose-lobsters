package dev.msfjarvis.lobsters.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.posts.HottestPosts
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel
import dev.msfjarvis.lobsters.util.IconResource
import kotlinx.coroutines.launch

@Composable
fun LobstersApp() {
  val viewModel: LobstersViewModel = viewModel()
  val coroutineScope = rememberCoroutineScope()
  val navController = rememberNavController()
  val hottestPosts = viewModel.posts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPosts.collectAsState()
  val hottestPostsListState = rememberLazyListState()

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute =
    navBackStackEntry?.arguments?.getString(KEY_ROUTE) ?: Destination.startDestination.route
  val currentDestination = Destination.getDestinationFromRoute(currentRoute)
  val navigateToDestination: (destination: Destination) -> Unit = { destination ->
    navController.navigate(destination.route) {
      launchSingleTop = true
      popUpTo(navController.graph.startDestination) { inclusive = false }
    }
  }
  val jumpToIndex: (Int) -> Unit = {
    coroutineScope.launch {
      hottestPostsListState.animateScrollToItem(it)
    }
  }

  Scaffold(
    bottomBar = {
      LobstersBottomNav(
        currentDestination,
        navigateToDestination,
        jumpToIndex,
      )
    },
  ) { innerPadding ->
    NavHost(navController, startDestination = Destination.startDestination.route) {
      composable(Destination.Hottest.route) {
        HottestPosts(
          posts = hottestPosts,
          listState = hottestPostsListState,
          isPostSaved = viewModel::isPostSaved,
          saveAction = viewModel::toggleSave,
          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        )
      }
      composable(Destination.Saved.route) {
        SavedPosts(
          posts = savedPosts,
          saveAction = viewModel::toggleSave,
          modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        )
      }
    }
  }
}

@Composable
fun LobstersBottomNav(
  currentDestination: Destination,
  navigateToDestination: (destination: Destination) -> Unit,
  jumpToIndex: (index: Int) -> Unit,
) {
  BottomNavigation(modifier = Modifier.testTag("LobstersBottomNav")) {
    Destination.values().forEach { screen ->
      BottomNavigationItem(
        icon = {
          IconResource(
            resourceId = screen.badgeRes,
            contentDescription = stringResource(screen.labelRes),
          )
        },
        label = { Text(stringResource(id = screen.labelRes)) },
        selected = currentDestination == screen,
        alwaysShowLabel = false,
        onClick = {
          if (screen != currentDestination) {
            navigateToDestination(screen)
          } else if (screen.route == Destination.Hottest.route) {
            jumpToIndex(0)
          }
        }
      )
    }
  }
}
