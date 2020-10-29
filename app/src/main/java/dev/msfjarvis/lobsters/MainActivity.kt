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
import androidx.compose.runtime.ambientOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.setContent
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
  val posts = viewModel.posts.collectAsState()
  val lastIndex = posts.value.lastIndex
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
            label = { Text(screen.label) },
            selected = currentRoute == screen.route,
            onClick = {
              // This is the equivalent to popUpTo the start destination
              navController.popBackStack(navController.graph.startDestination, false)

              // This if check gives us a "singleTop" behavior where we do not create a
              // second instance of the composable if we are already on that destination
              if (currentRoute != screen.route) {
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
        HottestPosts(lastIndex = lastIndex, urlLauncher = urlLauncher , viewModel = viewModel)
      }
      composable(Destination.Saved.route) {
        SavedPosts(urlLauncher = urlLauncher, viewModel = viewModel)
      }
    }
  }
}
