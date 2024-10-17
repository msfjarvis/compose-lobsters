/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.SearchActivity
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.getActivity
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.ui.navigation.any
import dev.msfjarvis.claw.android.ui.navigation.none
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LobstersPostsScreen(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val context = LocalContext.current
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val navController = rememberNavController()
  val navBackStackEntry = navController.currentBackStackEntryAsState().value
  val currentDestination = navBackStackEntry?.destination
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val postActions = remember { PostActions(context, urlLauncher, navController, viewModel) }
  val hazeState = remember { HazeState() }

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())

  val navigationType = ClawNavigationType.fromSize(windowSizeClass.widthSizeClass)

  val postIdOverride = context.getActivity()?.intent?.extras?.getString(MainActivity.NAVIGATION_KEY)
  LaunchedEffect(Unit) {
    if (postIdOverride != null) {
      navController.navigate(Comments(postIdOverride))
    }
  }

  val navItems =
    persistentListOf(
      NavigationItem(
        label = "Hottest",
        destination = Hottest,
        icon = Icons.Outlined.Whatshot,
        selectedIcon = Icons.Filled.Whatshot,
      ) {
        coroutineScope.launch {
          if (hottestPosts.itemCount > 0) hottestListState.animateScrollToItem(index = 0)
        }
      },
      NavigationItem(
        label = "Newest",
        destination = Newest,
        icon = Icons.Outlined.NewReleases,
        selectedIcon = Icons.Filled.NewReleases,
      ) {
        coroutineScope.launch {
          if (newestPosts.itemCount > 0) newestListState.animateScrollToItem(index = 0)
        }
      },
      NavigationItem(
        label = "Saved",
        destination = Saved,
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite,
      ) {
        coroutineScope.launch { if (savedPosts.isNotEmpty()) savedListState.scrollToItem(0) }
      },
    )
  val navDestinations = navItems.map(NavigationItem::destination).toPersistentList()

  Scaffold(
    topBar = {
      TopAppBar(
        modifier = Modifier.shadow(8.dp),
        navigationIcon = {
          if (
            navController.previousBackStackEntry != null && currentDestination.none(navDestinations)
          ) {
            IconButton(
              onClick = { if (!navController.popBackStack()) context.getActivity()?.finish() }
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Go back to previous screen",
              )
            }
          } else {
            Icon(
              painter = painterResource(id = R.drawable.ic_launcher_foreground),
              contentDescription = "The app icon for Claw",
              modifier = Modifier.size(48.dp),
            )
          }
        },
        title = {
          if (currentDestination.any(navDestinations)) {
            Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
          }
        },
        actions = {
          if (currentDestination.any(navDestinations)) {
            IconButton(
              onClick = { context.startActivity(Intent(context, SearchActivity::class.java)) }
            ) {
              Icon(imageVector = Icons.Filled.Search, contentDescription = "Search posts")
            }
            IconButton(onClick = { navController.navigate(Settings) }) {
              Icon(imageVector = Icons.Filled.Tune, contentDescription = "Settings")
            }
          }
        },
      )
    },
    bottomBar = {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
        ClawNavigationBar(
          navController = navController,
          items = navItems,
          isVisible = currentDestination.any(navDestinations),
          hazeState = hazeState,
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    Row {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
        ClawNavigationRail(
          navController = navController,
          items = navItems,
          isVisible = currentDestination.any(navDestinations),
        )
      }

      NavHost(
        navController = navController,
        startDestination = Hottest,
        // Make animations 2x faster than default specs
        enterTransition = { fadeIn(animationSpec = tween(350)) },
        exitTransition = { fadeOut(animationSpec = tween(350)) },
        modifier = Modifier.haze(hazeState),
      ) {
        composable<Hottest> {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            lazyPagingItems = hottestPosts,
            listState = hottestListState,
            postActions = postActions,
            contentPadding = contentPadding,
            onPostClick = {postId -> navController.navigate(Comments(postId))}
          )
        }
        composable<Newest> {
          setWebUri("https://lobste.rs/")
          NetworkPosts(
            lazyPagingItems = newestPosts,
            listState = newestListState,
            postActions = postActions,
            contentPadding = contentPadding,
            onPostClick = {postId -> navController.navigate(Comments(postId))}
          )
        }
        composable<Saved> {
          setWebUri(null)
          DatabasePosts(
            items = savedPosts,
            listState = savedListState,
            postActions = postActions,
            contentPadding = contentPadding,
          )
        }
        composable<Comments> { backStackEntry ->
          val postId = backStackEntry.toRoute<Comments>().postId
          setWebUri("https://lobste.rs/s/$postId")
          CommentsPage(
            postId = postId,
            postActions = postActions,
            htmlConverter = htmlConverter,
            getSeenComments = viewModel::getSeenComments,
            markSeenComments = viewModel::markSeenComments,
            contentPadding = contentPadding,
            openUserProfile = { navController.navigate(User(it)) },
          )
        }
        composable<User> { backStackEntry ->
          val username = backStackEntry.toRoute<User>().username
          setWebUri("https://lobste.rs/u/$username")
          UserProfile(
            username = username,
            getProfile = viewModel::getUserProfile,
            contentPadding = contentPadding,
            openUserProfile = { navController.navigate(User(it)) },
          )
        }
        composable<Settings> {
          SettingsScreen(
            openInputStream = context.contentResolver::openInputStream,
            openOutputStream = context.contentResolver::openOutputStream,
            openLibrariesScreen = { navController.navigate(AboutLibraries) },
            importPosts = viewModel::importPosts,
            exportPostsAsJson = viewModel::exportPostsAsJson,
            exportPostsAsHtml = viewModel::exportPostsAsHtml,
            snackbarHostState = snackbarHostState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize(),
          )
        }
        composable<AboutLibraries> {
          LibrariesContainer(contentPadding = contentPadding, modifier = Modifier.fillMaxSize())
        }
      }
    }
  }
}
