/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
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
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.decorations.TransparentSystemBars
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.res.ClawIcons
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalComposeUiApi::class,
  ExperimentalMaterial3Api::class,
)
@Composable
fun LobstersApp(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
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

  LobstersTheme(
    dynamicColor = true,
    providedValues = arrayOf(LocalUriHandler provides urlLauncher),
  ) {
    val navItems =
      persistentListOf(
        NavigationItem(
          label = "Hottest",
          route = Destinations.Hottest.route,
          icon = ClawIcons.Flame,
          selectedIcon = ClawIcons.FlameFilled,
        ) {
          coroutineScope.launch { hottestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Newest",
          route = Destinations.Newest.route,
          icon = ClawIcons.New,
          selectedIcon = ClawIcons.NewFilled,
        ) {
          coroutineScope.launch { newestListState.animateScrollToItem(index = 0) }
        },
        NavigationItem(
          label = "Saved",
          route = Destinations.Saved.route,
          icon = ClawIcons.HeartBorder,
          selectedIcon = ClawIcons.Heart,
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
      modifier = modifier.semantics { testTagsAsResourceId = true },
    ) { paddingValues ->
      NavHost(
        navController = navController,
        startDestination = Destinations.startDestination.route,
        modifier = Modifier.padding(paddingValues),
      ) {
        val uri = LobstersApi.BASE_URL
        composable(
          route = Destinations.Hottest.route,
          deepLinks =
            listOf(navDeepLink { uriPattern = uri }, navDeepLink { uriPattern = "$uri/" }),
        ) {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            lazyPagingItems = hottestPosts,
            listState = hottestListState,
            isPostSaved = viewModel::isPostSaved,
            postActions = postActions,
          )
        }
        composable(
          route = Destinations.Newest.route,
        ) {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            lazyPagingItems = newestPosts,
            listState = newestListState,
            isPostSaved = viewModel::isPostSaved,
            postActions = postActions,
          )
        }
        composable(Destinations.Saved.route) {
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
