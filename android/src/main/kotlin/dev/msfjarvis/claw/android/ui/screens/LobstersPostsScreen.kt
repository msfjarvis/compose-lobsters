/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
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
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.ClawTopBarMode
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.lists.SearchResultsList
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Login
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.NonStackable
import dev.msfjarvis.claw.android.ui.navigation.Reply
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.SentryNavigation3Traced
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.TagFiltering
import dev.msfjarvis.claw.android.ui.navigation.TopLevelDestination
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.android.viewmodel.SettingsViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.reply.ReplyScreen
import dev.msfjarvis.claw.common.tags.TagFilterViewModel
import dev.msfjarvis.claw.common.tags.TagList
import dev.msfjarvis.claw.common.user.UserProfile
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SEARCH_DEBOUNCE_MILLIS = 300L
private const val SEARCH_WEB_URI = "https://lobste.rs/search"
private const val HOTTEST_WEB_URI = "https://lobste.rs/"
private const val NEWEST_WEB_URI = "https://lobste.rs/newest"

enum class TopLevelBackAction {
  DismissSearch,
  PopNavigation,
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun handleTopLevelBack(
  isSearchActive: Boolean,
  isCurrentDestinationTopLevel: Boolean,
): TopLevelBackAction {
  return if (isSearchActive && isCurrentDestinationTopLevel) {
    TopLevelBackAction.DismissSearch
  } else {
    TopLevelBackAction.PopNavigation
  }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LobstersPostsScreen(
  uriHandler: UriHandler,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  deepLinkDestination: NavKey? = null,
  clearDeepLink: () -> Unit = {},
  viewModel: ClawViewModel = metroViewModel(),
  settingsViewModel: SettingsViewModel = metroViewModel(),
  tagFilterViewModel: TagFilterViewModel = metroViewModel(key = "tag_filter"),
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

  val savedPosts by viewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())
  val savedPostsCount by viewModel.savedPostsCount.collectAsStateWithLifecycle(0L)

  val filteredTags by tagFilterViewModel.filteredTags.collectAsStateWithLifecycle(persistentSetOf())
  val isLoggedIn by settingsViewModel.isLoggedIn.collectAsStateWithLifecycle(false)
  val username by settingsViewModel.username.collectAsStateWithLifecycle(null)
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var searchQuery by rememberSaveable { mutableStateOf("") }
  var lastExecutedSearchQuery by rememberSaveable { mutableStateOf<String?>(null) }
  val searchResultsListState = rememberLazyListState()
  val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
  val currentDestination = backStack.lastOrNull()
  val currentDestinationIsTopLevel = currentDestination is TopLevelDestination
  val normalizedSearchQuery = searchQuery.trim()
  val searchResultsBottomPadding =
    if (navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
      80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    } else {
      0.dp
    }

  val navItems =
    persistentListOf(
      NavigationItem(AppDestinations.HOTTEST) {
        coroutineScope.launch { hottestListState.animateScrollToItem(index = 0) }
      },
      NavigationItem(AppDestinations.NEWEST) {
        coroutineScope.launch { newestListState.animateScrollToItem(index = 0) }
      },
      NavigationItem(AppDestinations.SAVED) {
        coroutineScope.launch { if (savedPosts.isNotEmpty()) savedListState.scrollToItem(0) }
      },
    )
  // endregion

  val executeSearch = { query: String ->
    val normalizedQuery = query.trim()
    lastExecutedSearchQuery = normalizedQuery
    coroutineScope.launch { searchResultsListState.scrollToItem(0) }
    viewModel.searchQuery = normalizedQuery
    searchResults.refresh()
  }
  val dismissSearch = {
    isSearchActive = false
    searchQuery = ""
    lastExecutedSearchQuery = ""
    viewModel.searchQuery = ""
  }
  val navigateToDestination = { destination: NavKey, allowStacking: Boolean ->
    if (isSearchActive) {
      dismissSearch()
    }
    navigateTo(backStack, destination, allowStacking = allowStacking)
  }
  val startSearch = {
    isSearchActive = true
  }

  val postActions =
    remember(isSearchActive) {
      PostActions(context, uriHandler, viewModel) {
        navigateToDestination(Comments(it), false)
      }
    }

  LaunchedEffect(deepLinkDestination) {
    if (deepLinkDestination != null) {
      navigateToDestination(deepLinkDestination, true)
      clearDeepLink()
    }
  }

  LaunchedEffect(currentDestination, isSearchActive) {
    if (currentDestinationIsTopLevel) {
      when {
        isSearchActive -> setWebUri(SEARCH_WEB_URI)
        currentDestination == Hottest -> setWebUri(HOTTEST_WEB_URI)
        currentDestination == Newest -> setWebUri(NEWEST_WEB_URI)
        currentDestination == Saved -> setWebUri(null)
      }
    }
  }

  LaunchedEffect(currentDestinationIsTopLevel, isSearchActive) {
    if (!currentDestinationIsTopLevel && isSearchActive) {
      dismissSearch()
    }
  }

  LaunchedEffect(isSearchActive, normalizedSearchQuery) {
    if (!isSearchActive) return@LaunchedEffect
    delay(SEARCH_DEBOUNCE_MILLIS.milliseconds)
    if (normalizedSearchQuery != lastExecutedSearchQuery) {
      executeSearch(normalizedSearchQuery)
    }
  }

  LaunchedEffect(
    isSearchActive,
    normalizedSearchQuery,
    lastExecutedSearchQuery,
    viewModel.searchQuery,
  ) {
    if (!isSearchActive) return@LaunchedEffect
    if (normalizedSearchQuery.isBlank()) return@LaunchedEffect
    if (
      normalizedSearchQuery == lastExecutedSearchQuery &&
        viewModel.searchQuery != normalizedSearchQuery
    ) {
      executeSearch(normalizedSearchQuery)
    }
  }

  BackHandler(enabled = isSearchActive || backStack.size > 1) {
    when (
      handleTopLevelBack(
        isSearchActive = isSearchActive,
        isCurrentDestinationTopLevel = currentDestinationIsTopLevel,
      )
    ) {
      TopLevelBackAction.DismissSearch -> dismissSearch()
      TopLevelBackAction.PopNavigation -> popBackStack(backStack)
    }
  }

  Scaffold(
    topBar = {
      ClawAppBar(
        activity = activity,
        isTopLevel = currentDestinationIsTopLevel,
        mode =
          if (currentDestinationIsTopLevel && isSearchActive) {
            ClawTopBarMode.Searching(query = searchQuery, expanded = true, requestFocus = true)
          } else {
            ClawTopBarMode.Browsing
          },
        navigateTo = { destination -> navigateToDestination(destination, false) },
        popBackStack = { popBackStack(backStack) },
        onStartSearch = startSearch,
        onDismissSearch = dismissSearch,
        onExpandedChange = { expanded -> if (!expanded) dismissSearch() },
        onQueryChange = { searchQuery = it },
        onSearch = { query ->
          searchQuery = query
          executeSearch(query)
        },
      ) {
        if (
          normalizedSearchQuery.isNotBlank() && normalizedSearchQuery == lastExecutedSearchQuery
        ) {
          SearchResultsList(
            lazyPagingItems = searchResults,
            listState = searchResultsListState,
            postActions = postActions,
            filteredTags = filteredTags,
            contentPadding = PaddingValues(bottom = searchResultsBottomPadding),
          )
        }
      }
    },
    bottomBar = {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
        ClawNavigationBar(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { destination -> navigateToDestination(destination, false) },
          isVisible = currentDestinationIsTopLevel && !isSearchActive,
          hazeState = hazeState,
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    SentryNavigation3Traced(backStack = backStack)
    Row {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
        ClawNavigationRail(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { destination -> navigateToDestination(destination, false) },
          isVisible = currentDestinationIsTopLevel && !isSearchActive,
        )
      }
      NavDisplay(
        backStack = backStack,
        modifier = modifier.hazeSource(hazeState),
        sceneStrategies = listOf(listDetailStrategy),
        entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),
        onBack = {
          if (popBackStack(backStack) == null) {
            activity?.finish()
          }
        },
        predictivePopTransitionSpec = {
          slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
        },
        entryProvider =
          entryProvider {
            entry<Hottest>(
              metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
            ) {
              setWebUri(HOTTEST_WEB_URI)
              val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
              NetworkPosts(
                lazyPagingItems = hottestPosts,
                listState = hottestListState,
                postActions = postActions,
                contentPadding = contentPadding,
                filteredTags = filteredTags,
              )
            }
            entry<Newest>(
              metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { Placeholder() })
            ) {
              setWebUri(NEWEST_WEB_URI)
              val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
              NetworkPosts(
                lazyPagingItems = newestPosts,
                listState = newestListState,
                postActions = postActions,
                contentPadding = contentPadding,
                filteredTags = filteredTags,
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
              val commentsPostActions =
                remember(isSearchActive) {
                  PostActions(context, uriHandler, viewModel) {
                    navigateToDestination(Comments(it), true)
                  }
                }
              CommentsPage(
                postId = dest.postId,
                postActions = commentsPostActions,
                contentPadding = contentPadding,
                openUserProfile = { navigateToDestination(User(it), false) },
                openReplyScreen = { postId, commentId ->
                  navigateToDestination(Reply(postId, commentId), true)
                },
              )
            }
            entry<Reply>(metadata = ListDetailSceneStrategy.extraPane()) { dest ->
              ReplyScreen(
                commentId = dest.commentId,
                contentPadding = contentPadding,
                postId = dest.postId,
                onReplySubmitted = { popBackStack(backStack) },
                modifier = Modifier.fillMaxSize(),
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
                openUserProfile = { navigateToDestination(User(it), false) },
              )
            }
            entry<Settings>(metadata = ListDetailSceneStrategy.extraPane()) {
              SettingsScreen(
                openInputStream = context.contentResolver::openInputStream,
                openOutputStream = context.contentResolver::openOutputStream,
                openLibrariesScreen = { navigateToDestination(AboutLibraries, false) },
                openRepository = {
                  uriHandler.openUri("https://github.com/msfjarvis/compose-lobsters")
                },
                openTagFiltering = { navigateToDestination(TagFiltering, false) },
                openLoginScreen = { navigateToDestination(Login, false) },
                onLogout = { settingsViewModel.logout() },
                isLoggedIn = isLoggedIn,
                username = username,
                importPosts = viewModel::importPosts,
                exportPostsAsJson = viewModel::exportPostsAsJson,
                exportPostsAsHtml = viewModel::exportPostsAsHtml,
                savedPostsCount = savedPostsCount,
                snackbarHostState = snackbarHostState,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
              )
            }
            entry<Login>(metadata = ListDetailSceneStrategy.extraPane()) {
              LoginScreen(
                onLoginSuccess = settingsViewModel::saveCookie,
                popBackStack = { popBackStack(backStack) },
                modifier = Modifier.fillMaxSize(),
              )
            }
            entry<AboutLibraries>(metadata = ListDetailSceneStrategy.extraPane()) {
              LibrariesContainer(
                libraries = libraries,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
              )
            }
            entry<TagFiltering> {
              TagList(
                popBackStack = { popBackStack(backStack) },
                contentPadding = contentPadding,
              )
            }
          },
      )
    }
  }
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun navigateTo(
  backStack: MutableList<NavKey>,
  destination: NavKey,
  allowStacking: Boolean = false,
) {
  if (destination is TopLevelDestination) {
    backStack.clear()
    if (destination != Hottest) {
      backStack.add(Hottest)
    }
    backStack.add(destination)
    return
  }

  if (destination is NonStackable) {
    if (allowStacking && destination is Comments) {
      val lastItem = backStack.lastOrNull()
      if (lastItem is Comments && lastItem.postId == destination.postId) {
        return
      }
      backStack.add(destination)
      return
    }

    val existingEntry = backStack.firstOrNull {
      it is NonStackable && it::class.java.isAssignableFrom(destination::class.java)
    }

    if (existingEntry != null) {
      backStack.remove(existingEntry)
    }
  }

  backStack.add(destination)
}

private fun popBackStack(backStack: MutableList<NavKey>): NavKey? {
  if (backStack.size <= 1) {
    return null
  }

  return backStack.removeAt(backStack.lastIndex)
}

@Composable
private fun Placeholder(modifier: Modifier = Modifier) {
  Text(text = stringResource(R.string.no_post_selected), modifier = modifier)
}
