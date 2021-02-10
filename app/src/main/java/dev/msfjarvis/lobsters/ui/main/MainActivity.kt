package dev.msfjarvis.lobsters.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.posts.HottestPosts
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncher
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel
import dev.msfjarvis.lobsters.util.IconResource
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Providers(LocalUrlLauncher provides urlLauncher) {
        LobstersTheme {
          LobstersApp()
        }
      }
    }
  }
}

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

  Scaffold(
    bottomBar = {
      LobstersBottomNav(
        hottestPostsListState,
        coroutineScope,
        currentDestination,
        navigateToDestination,
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
  hottestPostsListState: LazyListState,
  coroutineScope: CoroutineScope,
  currentDestination: Destination,
  navigateToDestination: (destination: Destination) -> Unit,
) {
  BottomNavigation {
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
        alwaysShowLabels = false,
        onClick = {
          if (screen != currentDestination) {
            navigateToDestination(screen)
          } else if (screen.route == Destination.Hottest.route) {
            coroutineScope.launch {
              hottestPostsListState.snapToItemIndex(0)
            }
          }
        }
      )
    }
  }
}
