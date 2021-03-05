package dev.msfjarvis.lobsters.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.posts.HottestPosts
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel
import dev.msfjarvis.lobsters.util.IconResource
import kotlinx.coroutines.launch

@Composable
fun LobstersApp() {
  val viewModel: LobstersViewModel = viewModel()
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
  val jumpToIndex: suspend (Int) -> Unit = {
    hottestPostsListState.animateScrollToItem(it)
  }

  Scaffold(
    topBar = {
      LobstersTopBar(
        currentDestination = currentDestination,
        reloadPosts = { viewModel.reloadPosts() },
      )
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LobstersTopBar(
  currentDestination: Destination,
  reloadPosts: () -> Unit,
) {
  TopAppBar(
    title = {
      Text(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.padding(vertical = 8.dp),
      )
    },
    actions = {
      if (currentDestination == Destination.Hottest) {
        IconResource(
          resourceId = R.drawable.ic_refresh_24px,
          contentDescription = stringResource(id = R.string.refresh_posts_content_description),
          modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { reloadPosts() },
        )
      }
    }
  )
}

@Composable
fun LobstersBottomNav(
  currentDestination: Destination,
  navigateToDestination: (destination: Destination) -> Unit,
  jumpToIndex: suspend (index: Int) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
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
        modifier = Modifier.testTag(screen.name),
        alwaysShowLabel = false,
        onClick = {
          if (screen != currentDestination) {
            navigateToDestination(screen)
          } else if (screen.route == Destination.Hottest.route) {
            coroutineScope.launch { jumpToIndex(0) }
          }
        }
      )
    }
  }
}
