/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.NonStackable
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.TagFiltering
import dev.msfjarvis.claw.android.ui.navigation.TopLevelDestination
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.tags.TagList
import dev.msfjarvis.claw.common.user.UserProfile
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LobstersPostsScreen(
  uriHandler: UriHandler,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  postIdFromDeepLink: String? = null,
  viewModel: ClawViewModel = metroViewModel(),
) {
  val navigationType = ClawNavigationType.fromSize(windowSizeClass.widthSizeClass)

  val libraries by produceLibraries()
  val backStack = rememberNavBackStack(Hottest)
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
  val savedPostsCount by viewModel.savedPostsCount.collectAsStateWithLifecycle(0L)

  LaunchedEffect(postIdFromDeepLink) {
    if (postIdFromDeepLink != null) {
      navigateTo(backStack, Comments(postIdFromDeepLink))
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
    PostActions(context, uriHandler, viewModel) { backStack.add(Comments(it)) }
  }

  BackHandler(enabled = backStack.size > 1) { backStack.removeAt(backStack.lastIndex) }

  Scaffold(
    topBar = {
      ClawAppBar(
        activity = activity,
        isTopLevel = backStack.lastOrNull() is TopLevelDestination,
        navigateTo = { destination -> navigateTo(backStack, destination) },
        popBackStack = { backStack.removeAt(backStack.lastIndex) },
      )
    },
    bottomBar = {
      val currentDestination = backStack.lastOrNull()
      AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
        ClawNavigationBar(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { destination -> navigateTo(backStack, destination) },
          isVisible = backStack.lastOrNull() is TopLevelDestination,
          hazeState = hazeState,
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    Row {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
        val currentDestination = backStack.lastOrNull()
        ClawNavigationRail(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { destination -> navigateTo(backStack, destination) },
          isVisible = backStack.lastOrNull() is TopLevelDestination,
        )
      }
      NavDisplay(
        backStack = backStack,
        modifier = modifier.hazeSource(hazeState),
        sceneStrategy = listDetailStrategy,
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        onBack = { backStack.removeAt(backStack.lastIndex) },
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
                openUserProfile = { backStack.add(User(it)) },
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
                openUserProfile = { backStack.add(User(it)) },
              )
            }
            entry<Settings>(metadata = ListDetailSceneStrategy.extraPane()) {
              SettingsScreen(
                openInputStream = context.contentResolver::openInputStream,
                openOutputStream = context.contentResolver::openOutputStream,
                openLibrariesScreen = { backStack.add(AboutLibraries) },
                openRepository = {
                  uriHandler.openUri("https://github.com/msfjarvis/compose-lobsters")
                },
                importPosts = viewModel::importPosts,
                exportPostsAsJson = viewModel::exportPostsAsJson,
                exportPostsAsHtml = viewModel::exportPostsAsHtml,
                savedPostsCount = savedPostsCount,
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

private fun navigateTo(backStack: MutableList<NavKey>, destination: NavKey) {
  if (destination is TopLevelDestination) {
    backStack.clear()
    if (destination != Hottest) {
      backStack.add(Hottest)
    }
  }
  val existingEntry =
    backStack.firstOrNull {
      it is NonStackable && it::class.java.isAssignableFrom(destination::class.java)
    }
  if (destination is NonStackable && existingEntry != null) {
    backStack.remove(existingEntry)
  }
  backStack.add(destination)
}

@Composable
private fun Placeholder(modifier: Modifier = Modifier) {
  Text(text = "Placeholder", modifier = modifier)
}
