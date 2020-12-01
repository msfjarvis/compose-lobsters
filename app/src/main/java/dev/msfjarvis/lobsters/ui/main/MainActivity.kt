package dev.msfjarvis.lobsters.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.util.IconResource
import dev.msfjarvis.lobsters.ui.viewmodel.LobstersViewModel
import dev.msfjarvis.lobsters.ui.navigation.Destination
import dev.msfjarvis.lobsters.ui.posts.HottestPosts
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import dev.msfjarvis.lobsters.ui.posts.SavedPosts
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncher
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncherAmbient
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  @Inject lateinit var urlLauncher: UrlLauncher

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      Providers(UrlLauncherAmbient provides urlLauncher) {
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
  val navController = rememberNavController()
  val hottestPosts by viewModel.posts.collectAsState()
  val savedPosts by viewModel.savedPosts.collectAsState()

  Scaffold(
    bottomBar = {
      LobstersBottomNav(navController)
    },
  ) { innerPadding ->
    val hottestPostsListState = rememberLazyListState()
    NavHost(navController, startDestination = Destination.startDestination.route) {
      composable(Destination.Hottest.route) {
        HottestPosts(
          posts = hottestPosts,
          listState = hottestPostsListState,
          overscrollAction = viewModel::getMorePosts,
          saveAction = viewModel::savePost,
          modifier = Modifier.padding(bottom = innerPadding.bottom),
        )
      }
      composable(Destination.Saved.route) {
        SavedPosts(
          posts = savedPosts,
          saveAction = viewModel::removeSavedPost,
          modifier = Modifier.padding(bottom = innerPadding.bottom),
        )
      }
    }
  }
}

@Composable
fun LobstersBottomNav(
  navController: NavHostController,
) {
  BottomNavigation {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
      navBackStackEntry?.arguments?.getString(KEY_ROUTE) ?: Destination.startDestination.route
    Destination.values().forEach { screen ->
      BottomNavigationItem(
        icon = { IconResource(resourceId = screen.badgeRes) },
        label = { Text(stringResource(id = screen.labelRes)) },
        selected = currentRoute == screen.route,
        alwaysShowLabels = false,
        onClick = {
          if (currentRoute == screen.route) return@BottomNavigationItem
          navController.popBackStack(navController.graph.startDestination, false)
          if (screen.route != Destination.startDestination.route) {
            navController.navigate(screen.route)
          }
        }
      )
    }
  }
}
