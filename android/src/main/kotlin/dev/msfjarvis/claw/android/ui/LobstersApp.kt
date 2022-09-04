package dev.msfjarvis.claw.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.comments.LocalHTMLConverter
import dev.msfjarvis.claw.common.res.ClawIcons
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.common.ui.surfaceColorAtNavigationBarElevation
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.MaterialMotionNavHost
import soup.compose.material.motion.navigation.composable
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun LobstersApp(
  viewModel: ClawViewModel = viewModel(),
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String?) -> Unit,
) {
  val systemUiController = rememberSystemUiController()
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val navController = rememberMaterialMotionNavController()
  val coroutineScope = rememberCoroutineScope()
  val postActions = rememberPostActions(urlLauncher, navController, viewModel)
  val currentDestination = currentNavigationDestination(navController)
  val context = LocalContext.current

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPosts.collectAsState(emptyMap())

  LobstersTheme(
    providedValues =
      arrayOf(
        LocalUriHandler provides urlLauncher,
        LocalHTMLConverter provides htmlConverter,
      ),
    colorScheme = decideColorScheme(LocalContext.current),
  ) {
    val currentUiMode = LocalConfiguration.current.uiMode
    val systemBarsColor = MaterialTheme.colorScheme.surfaceColorAtNavigationBarElevation()
    val backgroundColor = MaterialTheme.colorScheme.background
    val navItems =
      listOf(
        NavigationItem(
          label = "Hottest",
          route = Destinations.Hottest.getRoute(),
          icon = ClawIcons.Flame,
          selectedIcon = ClawIcons.FlameFilled,
        ) {
          coroutineScope.launch { hottestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Newest",
          route = Destinations.Newest.getRoute(),
          icon = ClawIcons.New,
          selectedIcon = ClawIcons.NewFilled,
        ) {
          coroutineScope.launch { newestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Saved",
          route = Destinations.Saved.getRoute(),
          icon = ClawIcons.HeartBorder,
          selectedIcon = ClawIcons.Heart,
        ) {
          coroutineScope.launch { savedListState.animateScrollToItem(index = 0) }
        },
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
          backgroundColor = systemBarsColor,
          navigationIcon = {
            if (navItems.none { it.route == currentDestination }) {
              IconButton(
                onClick = { if (!navController.popBackStack()) context.getActivity()?.finish() }
              ) {
                Icon(
                  painter = ClawIcons.ArrowBack,
                  contentDescription = "Go back to previous screen",
                )
              }
            }
          },
          title = {
            if (navItems.any { it.route == currentDestination }) {
              Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
            }
          },
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
      MaterialMotionNavHost(
        navController = navController,
        startDestination = Destinations.startDestination.getRoute(),
        modifier = Modifier.padding(paddingValues),
      ) {
        val uri = LobstersApi.BASE_URL
        composable(
          route = Destinations.Hottest.getRoute(),
          deepLinks =
            listOf(navDeepLink { uriPattern = uri }, navDeepLink { uriPattern = "$uri/" }),
        ) {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            items = hottestPosts,
            listState = hottestListState,
            isPostSaved = viewModel::isPostSaved,
            reloadPosts = viewModel::refreshHottestPosts,
            postActions = postActions,
          )
        }
        composable(
          route = Destinations.Newest.getRoute(),
        ) {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            items = newestPosts,
            listState = newestListState,
            isPostSaved = viewModel::isPostSaved,
            reloadPosts = viewModel::refreshNewestPosts,
            postActions = postActions,
          )
        }
        composable(Destinations.Saved.getRoute()) {
          setWebUri(null)
          DatabasePosts(
            items = savedPosts,
            listState = savedListState,
            postActions = postActions,
          )
        }
        composable(
          route = Destinations.Comments.getRoute("{postId}"),
          arguments = listOf(navArgument("postId") { type = NavType.StringType }),
          deepLinks =
            listOf(
              navDeepLink { uriPattern = "$uri/s/{postId}/.*" },
              navDeepLink { uriPattern = "$uri/s/{postId}" },
            ),
        ) { backStackEntry ->
          val postId = requireNotNull(backStackEntry.arguments?.getString("postId"))
          setWebUri("https://lobste.rs/s/$postId")
          CommentsPage(
            postId = postId,
            getDetails = viewModel::getPostComments,
            postActions = postActions,
          )
        }
        composable(
          route = Destinations.User.getRoute("{username}"),
          arguments = listOf(navArgument("username") { type = NavType.StringType }),
          deepLinks = listOf(navDeepLink { uriPattern = "$uri/u/{username}" }),
        ) { backStackEntry ->
          val username = requireNotNull(backStackEntry.arguments?.getString("username"))
          UserProfile(
            username = username,
            getProfile = viewModel::getUserProfile,
          )
        }
      }
    }
  }
}
