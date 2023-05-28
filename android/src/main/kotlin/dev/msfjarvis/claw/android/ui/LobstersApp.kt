/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.decorations.TransparentSystemBars
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LobstersApp(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val postActions = rememberPostActions(urlLauncher, navController, viewModel)
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = backStackEntry?.destination?.route
  val context = LocalContext.current

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPosts.collectAsState(persistentMapOf())

  val navigationType = ClawNavigationType.fromSize(windowSizeClass.widthSizeClass)

  LobstersTheme(
    dynamicColor = true,
    providedValues = arrayOf(LocalUriHandler provides urlLauncher),
  ) {
    val navItems =
      persistentListOf(
        NavigationItem(
          label = "Hottest",
          route = Destinations.Hottest.route,
          icon = rememberVectorPainter(Icons.Outlined.Whatshot),
          selectedIcon = rememberVectorPainter(Icons.Filled.Whatshot),
        ) {
          coroutineScope.launch { hottestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Newest",
          route = Destinations.Newest.route,
          icon = rememberVectorPainter(Icons.Outlined.NewReleases),
          selectedIcon = rememberVectorPainter(Icons.Filled.NewReleases),
        ) {
          coroutineScope.launch { newestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Saved",
          route = Destinations.Saved.route,
          icon = rememberVectorPainter(Icons.Outlined.FavoriteBorder),
          selectedIcon = rememberVectorPainter(Icons.Filled.Favorite),
        ) {
          coroutineScope.launch { savedListState.animateScrollToItem(index = 0) }
        },
      )

    TransparentSystemBars()

    Scaffold(
      topBar = {
        ClawAppBar(
          navigationIcon = {
            if (navItems.none { it.route == currentDestination }) {
              IconButton(
                onClick = { if (!navController.popBackStack()) context.getActivity()?.finish() }
              ) {
                Icon(
                  painter = rememberVectorPainter(Icons.Outlined.NavigateBefore),
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
        AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
          ClawNavigationBar(
            navController = navController,
            items = navItems,
            isVisible = navItems.any { it.route == currentDestination },
          )
        }
      },
      modifier = modifier.semantics { testTagsAsResourceId = true },
    ) { paddingValues ->
      Row(modifier = Modifier.padding(paddingValues)) {
        AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
          ClawNavigationRail(
            navController = navController,
            items = navItems,
            isVisible = navItems.any { it.route == currentDestination },
          )
        }

        NavHost(
          navController = navController,
          startDestination = Destinations.startDestination.route,
        ) {
          val uri = LobstersApi.BASE_URL
          animatedScreenComposable(route = Destinations.Hottest.route) {
            setWebUri("https://lobste.rs/")
            NetworkPosts(
              lazyPagingItems = hottestPosts,
              listState = hottestListState,
              isPostSaved = viewModel::isPostSaved,
              postActions = postActions,
            )
          }
          animatedScreenComposable(route = Destinations.Newest.route) {
            setWebUri("https://lobste.rs/")
            NetworkPosts(
              lazyPagingItems = newestPosts,
              listState = newestListState,
              isPostSaved = viewModel::isPostSaved,
              postActions = postActions,
            )
          }
          animatedScreenComposable(route = Destinations.Saved.route) {
            setWebUri(null)
            DatabasePosts(
              items = savedPosts,
              listState = savedListState,
              postActions = postActions,
            )
          }
          composable(
            route = Destinations.Comments.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType }),
            deepLinks =
              listOf(
                navDeepLink { uriPattern = "$uri/s/${Destinations.Comments.placeholder}/.*" },
                navDeepLink { uriPattern = "$uri/s/${Destinations.Comments.placeholder}" },
              ),
            enterTransition = { slideInVertically(initialOffsetY = { -it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { -it }) },
            popEnterTransition = { slideInVertically(initialOffsetY = { -it }) },
            popExitTransition = { slideOutVertically(targetOffsetY = { -it }) },
          ) { backStackEntry ->
            val postId = requireNotNull(backStackEntry.arguments?.getString("postId"))
            setWebUri("https://lobste.rs/s/$postId")
            CommentsPage(
              postId = postId,
              postActions = postActions,
              htmlConverter = htmlConverter,
              getSeenComments = viewModel::getSeenComments,
              markSeenComments = viewModel::markSeenComments,
            )
          }
          composable(
            route = Destinations.User.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType }),
            deepLinks =
              listOf(navDeepLink { uriPattern = "$uri/u/${Destinations.User.placeholder}" }),
          ) { backStackEntry ->
            val username = requireNotNull(backStackEntry.arguments?.getString("username"))
            setWebUri("https://lobste.rs/u/$username")
            UserProfile(
              username = username,
              getProfile = viewModel::getUserProfile,
            )
          }
        }
      }
    }
  }
}

@Suppress("NOTHING_TO_INLINE") // Not doing it for performance
private inline fun NavGraphBuilder.animatedScreenComposable(
  route: String,
  arguments: List<NamedNavArgument> = emptyList(),
  deepLinks: List<NavDeepLink> = emptyList(),
  noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
  composable(
    route = route,
    arguments = arguments,
    deepLinks = deepLinks,
    enterTransition = { slideInVertically(initialOffsetY = { it }) },
    exitTransition = { slideOutVertically(targetOffsetY = { it }) },
    popEnterTransition = { slideInVertically(initialOffsetY = { it }) },
    popExitTransition = { slideOutVertically(targetOffsetY = { it }) },
    content = content,
  )
}
