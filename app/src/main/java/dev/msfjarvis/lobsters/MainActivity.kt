package dev.msfjarvis.lobsters

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.msfjarvis.lobsters.compose.utils.IconResource
import dev.msfjarvis.lobsters.data.LobstersViewModel
import dev.msfjarvis.lobsters.ui.Destination
import dev.msfjarvis.lobsters.ui.HottestPosts
import dev.msfjarvis.lobsters.ui.LobstersTheme
import dev.msfjarvis.lobsters.ui.SavedPosts
import dev.msfjarvis.lobsters.urllauncher.UrlLauncher
import dev.msfjarvis.lobsters.urllauncher.UrlLauncherAmbient
import javax.inject.Inject

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
  viewModel: LobstersViewModel,
) {
  val navController = rememberNavController()
  val destinations = arrayOf(Destination.Hottest, Destination.Saved)

  Scaffold(
    bottomBar = {
      BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        destinations.forEach { screen ->
          BottomNavigationItem(
            icon = {
              IconResource(
                resourceId = when (screen) {
                  Destination.Hottest -> R.drawable.ic_whatshot_24px
                  Destination.Saved -> R.drawable.ic_favorite_24px
                }
              )
            },
            label = { Text(stringResource(id = screen.labelRes)) },
            selected = currentRoute == screen.route,
            onClick = {
              // This if check gives us a "singleTop" behavior where we do not create a
              // second instance of the composable if we are already on that destination
              if (currentRoute != screen.route) {
                // This is the equivalent to popUpTo the start destination
                navController.popBackStack(navController.graph.startDestination, false)
                navController.navigate(screen.route)
              }
            }
          )
        }
      }
    },
  ) {
    NavHost(navController, startDestination = Destination.Hottest.route) {
      composable(Destination.Hottest.route) {
        HottestPosts(viewModel = viewModel)
      }
      composable(Destination.Saved.route) {
        SavedPosts(viewModel = viewModel)
      }
    }
  }
}
