/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.ClawBackStack
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.TagFiltering
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.tags.TagList
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LobstersPostsScreen(
  urlLauncher: UrlLauncher,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val libraries by produceLibraries()
  val clawBackStack = ClawBackStack(Hottest)
  val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

  // region Pain
  val context = LocalContext.current
  val activity = LocalActivity.current
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val hazeState = remember { HazeState() }

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())

  val navigationType = ClawNavigationType.fromSize(windowSizeClass.widthSizeClass)

  val postIdOverride = activity?.intent?.extras?.getString(MainActivity.NAVIGATION_KEY)
  LaunchedEffect(Unit) {
    if (postIdOverride != null) {
      clawBackStack.add(Comments(postIdOverride))
    }
  }

  val navItems =
    persistentListOf(
      NavigationItem(AppDestinations.HOTTEST) {
        coroutineScope.launch {
          if (hottestPosts.itemCount > 0) hottestListState.animateScrollToItem(index = 0)
        }
      },
      NavigationItem(AppDestinations.NEWEST) {
        coroutineScope.launch {
          if (newestPosts.itemCount > 0) newestListState.animateScrollToItem(index = 0)
        }
      },
      NavigationItem(AppDestinations.SAVED) {
        coroutineScope.launch { if (savedPosts.isNotEmpty()) savedListState.scrollToItem(0) }
      },
    )
  // endregion

  val postActions = remember {
    PostActions(context, urlLauncher, viewModel) { clawBackStack.add(Comments(it)) }
  }

  Scaffold(
    topBar = {
      ClawAppBar(
        activity = activity,
        isTopLevel = clawBackStack.isOnTopLevelDestination(),
        navigateTo = { clawBackStack.add(it) },
        popBackStack = { clawBackStack.removeLastOrNull() },
      )
    },
    bottomBar = {
      val currentDestination = clawBackStack.firstOrNull()
      AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
        ClawNavigationBar(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { clawBackStack.add(it) },
          isVisible = clawBackStack.isOnTopLevelDestination(),
          hazeState = hazeState,
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    Row {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
        val currentDestination = clawBackStack.firstOrNull()
        ClawNavigationRail(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { clawBackStack.add(it) },
          isVisible = clawBackStack.isOnTopLevelDestination(),
        )
      }
      NavDisplay(
        backStack = clawBackStack.backStack,
        modifier = modifier.hazeSource(hazeState),
        sceneStrategy = listDetailStrategy,
        onBack = { clawBackStack.removeLastOrNull() },
        predictivePopTransitionSpec = {
          slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
        },
        entryProvider =
          entryProvider {
            entry<Hottest>(
              metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
            ) {
              setWebUri("https://lobste.rs/")
              NetworkPosts(
                lazyPagingItems = hottestPosts,
                listState = hottestListState,
                postActions = postActions,
                contentPadding = contentPadding,
              )
            }
            entry<Newest>(
              metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
            ) {
              setWebUri("https://lobste.rs/")
              NetworkPosts(
                lazyPagingItems = newestPosts,
                listState = newestListState,
                postActions = postActions,
                contentPadding = contentPadding,
              )
            }
            entry<Saved>(
              metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
            ) {
              setWebUri(null)
              DatabasePosts(
                items = savedPosts,
                listState = savedListState,
                postActions = postActions,
                contentPadding = contentPadding,
              )
            }
            entry<Comments>(metadata = ListDetailSceneStrategy.detailPane()) { dest ->
              CommentsPage(
                postId = dest.postId,
                postActions = postActions,
                contentPadding = contentPadding,
                openUserProfile = { clawBackStack.add(User(it)) },
              )
            }
            entry<User>(
              metadata =
                NavDisplay.transitionSpec {
                  slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(200),
                  ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } +
                  NavDisplay.popTransitionSpec {
                    // Slide old content down, revealing the new content in place underneath
                    EnterTransition.None togetherWith
                      slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
                  } +
                  ListDetailSceneStrategy.extraPane()
            ) { dest ->
              UserProfile(
                username = dest.username,
                contentPadding = contentPadding,
                openUserProfile = { clawBackStack.add(User(it)) },
              )
            }
            entry<Settings>(metadata = ListDetailSceneStrategy.extraPane()) {
              SettingsScreen(
                openInputStream = context.contentResolver::openInputStream,
                openOutputStream = context.contentResolver::openOutputStream,
                openLibrariesScreen = { clawBackStack.add(AboutLibraries) },
                importPosts = viewModel::importPosts,
                exportPostsAsJson = viewModel::exportPostsAsJson,
                exportPostsAsHtml = viewModel::exportPostsAsHtml,
                snackbarHostState = snackbarHostState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
              )
            }
            entry<Search>(metadata = ListDetailSceneStrategy.extraPane()) {
              setWebUri("https://lobste.rs/search")
              SearchScreen(
                viewModel = viewModel,
                postActions = postActions,
                contentPadding = contentPadding,
              )
            }
            entry<AboutLibraries>(metadata = ListDetailSceneStrategy.extraPane()) {
              LibrariesContainer(
                libraries = libraries,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
              )
            }
            entry<TagFiltering> { TagList(contentPadding = contentPadding) }
          },
      )
    }
  }
}

@Composable
private fun Placeholder(modifier: Modifier = Modifier) {
  Text(text = "Placeholder", modifier = modifier)
}
