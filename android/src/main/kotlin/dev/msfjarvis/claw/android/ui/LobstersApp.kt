package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.HottestPosts
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.R as commonR
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.comments.LocalHTMLConverter
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobstersApp(
  viewModel: ClawViewModel = viewModel(),
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String?) -> Unit,
) {
  val systemUiController = rememberSystemUiController()
  val networkListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val postActions = rememberPostActions(urlLauncher, navController, viewModel)
  val currentDestination by currentNavigationDestination(navController)

  val networkPosts = viewModel.pagerFlow.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPosts.collectAsState(emptyList())

  LobstersTheme(
    LocalUriHandler provides urlLauncher,
    LocalHTMLConverter provides htmlConverter,
    colorScheme = decideColorScheme(LocalContext.current),
  ) {
    ProvideWindowInsets {
      val currentUiMode = LocalConfiguration.current.uiMode
      val systemBarsColor = MaterialTheme.colorScheme.surfaceColorAtNavigationBarElevation()
      val backgroundColor = MaterialTheme.colorScheme.background
      val navItems =
        listOf(
          NavigationItem(
            label = "Hottest",
            route = Destinations.Hottest.getRoute(),
            icon = painterResource(R.drawable.ic_whatshot_24dp),
          ) { coroutineScope.launch { networkListState.animateScrollToItem(index = 0) } },
          NavigationItem(
            label = "Saved",
            route = Destinations.Saved.getRoute(),
            icon = painterResource(commonR.drawable.ic_favorite_24dp),
          ) { coroutineScope.launch { savedListState.animateScrollToItem(index = 0) } },
        )

      SideEffect { systemUiController.setStatusBarColor(color = systemBarsColor) }

      // Track UI mode as a key to force a navbar color update when dark theme is toggled
      LaunchedEffect(currentDestination, currentUiMode) {
        val color =
          if (navItems.none { item -> item.route == currentDestination }) {
            backgroundColor
          } else {
            systemBarsColor
          }
        systemUiController.setNavigationBarColor(color = color)
      }

      Scaffold(
        topBar = {
          ClawAppBar(
            modifier = Modifier.statusBarsPadding(),
            backgroundColor = systemBarsColor,
          )
        },
        bottomBar = {
          ClawNavigationBar(
            navController = navController,
            items = navItems,
            isVisible = navItems.any { it.route == currentDestination },
          )
        },
      ) { paddingValues ->
        NavHost(
          navController,
          startDestination = Destinations.startDestination.getRoute(),
        ) {
          composable(Destinations.Hottest.getRoute()) {
            setWebUri("https://lobste.rs/")
            HottestPosts(
              items = networkPosts,
              listState = networkListState,
              isPostSaved = viewModel::isPostSaved,
              reloadPosts = viewModel::reloadPosts,
              postActions = postActions,
              modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            )
          }
          composable(Destinations.Saved.getRoute()) {
            setWebUri(null)
            DatabasePosts(
              items = savedPosts,
              listState = savedListState,
              isSaved = viewModel::isPostSaved,
              postActions = postActions,
              modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            )
          }
          composable(Destinations.Comments.getRoute("{postId}")) { backStackEntry ->
            val postId = requireNotNull(backStackEntry.arguments?.getString("postId"))
            setWebUri("https://lobste.rs/s/$postId")
            CommentsPage(
              postId = postId,
              getDetails = viewModel::getPostComments,
              modifier = Modifier.navigationBarsPadding(),
            )
          }
        }
      }
    }
  }
}
