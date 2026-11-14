/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings as AndroidSettings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.reminders.DEFAULT_DAILY_SAVED_POST_REMINDER_TIME
import dev.msfjarvis.claw.android.ui.decorations.ClawAppBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.ClawTopBarMode
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.lists.SearchResultsList
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.CommentsHeaderData
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Login
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Reply
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.TagFiltering
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.ui.updates.InAppUpdates
import dev.msfjarvis.claw.android.viewmodel.SearchViewModel
import dev.msfjarvis.claw.android.viewmodel.SettingsViewModel
import dev.msfjarvis.claw.feature.auth.impl.navigation.loginNavigationEntryContributor
import dev.msfjarvis.claw.feature.discussion.impl.navigation.discussionNavigationEntryContributor
import dev.msfjarvis.claw.feature.posts.impl.PostActions
import dev.msfjarvis.claw.feature.posts.impl.PostsViewModel
import dev.msfjarvis.claw.feature.posts.impl.navigation.AppDestinations
import dev.msfjarvis.claw.feature.posts.impl.navigation.postsNavigationEntryContributor
import dev.msfjarvis.claw.feature.profile.impl.navigation.profileNavigationEntryContributor
import dev.msfjarvis.claw.feature.search.api.SearchUiState
import dev.msfjarvis.claw.feature.settings.impl.navigation.settingsNavigationEntryContributor
import dev.msfjarvis.claw.feature.tags.impl.TagFilterViewModel
import dev.msfjarvis.claw.feature.tags.impl.TagList
import dev.msfjarvis.claw.model.UIPost
import dev.msfjarvis.claw.navigation.api.NavigationEntryContributor
import dev.msfjarvis.claw.navigation.api.TopLevelDestination
import dev.msfjarvis.claw.navigation.impl.BackStackNavigator
import dev.msfjarvis.claw.navigation.impl.NavigationHost
import dev.zacsweers.metrox.viewmodel.metroViewModel
import io.sentry.compose.navigation3.SentryBackStackEntry
import io.sentry.compose.navigation3.SentryNavEffect
import io.sentry.compose.navigation3.SentryNavOptions
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val SEARCH_WEB_URI = "https://lobste.rs/search"
private const val HOTTEST_WEB_URI = "https://lobste.rs/"
private const val NEWEST_WEB_URI = "https://lobste.rs/newest"

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun LobstersPostsScreen(
  uriHandler: UriHandler,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  deepLinkDestination: NavKey? = null,
  clearDeepLink: () -> Unit = {},
  searchViewModel: SearchViewModel = metroViewModel(),
  postsViewModel: PostsViewModel = metroViewModel(),
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

  val savedPosts by postsViewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())

  val filteredTags by tagFilterViewModel.filteredTags.collectAsStateWithLifecycle(persistentSetOf())
  val isLoggedIn by settingsViewModel.isLoggedIn.collectAsStateWithLifecycle(false)
  val username by settingsViewModel.username.collectAsStateWithLifecycle(null)
  val savedPostsCount by settingsViewModel.savedPostsCount.collectAsStateWithLifecycle(0L)
  val isDailySavedPostReminderEnabled by
    settingsViewModel.isDailySavedPostReminderEnabled.collectAsStateWithLifecycle(false)
  val dailySavedPostReminderTime by
    settingsViewModel.dailySavedPostReminderTime.collectAsStateWithLifecycle(
      DEFAULT_DAILY_SAVED_POST_REMINDER_TIME
    )
  val notificationPermissionDeniedMessage =
    stringResource(R.string.daily_saved_post_notification_permission_denied)
  val notificationSnackbarActionLabel = stringResource(R.string.settings)
  val notificationPermissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
      if (granted) {
        settingsViewModel.setDailySavedPostReminderEnabled(true)
      } else {
        coroutineScope.launch {
          val result =
            snackbarHostState.showSnackbar(
              message = notificationPermissionDeniedMessage,
              actionLabel = notificationSnackbarActionLabel,
            )
          if (result == SnackbarResult.ActionPerformed) {
            context.startActivity(
              Intent(AndroidSettings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(AndroidSettings.EXTRA_APP_PACKAGE, context.packageName)
            )
          }
        }
      }
    }
  var savedSearchState by
    rememberSaveable(stateSaver = SearchUiStateSaver) { mutableStateOf(SearchUiState()) }
  var hasRestoredSearchState by remember(searchViewModel) { mutableStateOf(false) }
  val searchState by searchViewModel.uiState.collectAsStateWithLifecycle(SearchUiState())
  LaunchedEffect(searchViewModel) {
    searchViewModel.restoreUiState(savedSearchState)
    hasRestoredSearchState = true
  }
  SideEffect {
    if (hasRestoredSearchState) savedSearchState = searchState
  }
  val isSearchActive = searchState.isActive
  val searchQuery = searchState.query
  val lastExecutedSearchQuery = searchState.lastExecutedQuery
  val searchResultsListState = rememberLazyListState()
  val searchResults = searchViewModel.searchResults.collectAsLazyPagingItems()
  val currentDestination = backStack.lastOrNull()
  val currentDestinationIsTopLevel = currentDestination is TopLevelDestination
  val normalizedSearchQuery = searchQuery.trim()
  val searchResultsBottomPadding =
    if (navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
      80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    } else {
      0.dp
    }

  val onDailySavedPostReminderEnabledChange: (Boolean) -> Unit = { enabled ->
    if (
      !enabled ||
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED
    ) {
      settingsViewModel.setDailySavedPostReminderEnabled(enabled)
    } else {
      notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
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
    searchViewModel.executeSearch(query)
  }
  val dismissSearch = {
    searchViewModel.dismissSearch()
  }
  val navigateToDestination = { destination: NavKey, allowStacking: Boolean ->
    if (isSearchActive) {
      dismissSearch()
    }
    navigateTo(backStack, destination, allowStacking = allowStacking)
  }
  val startSearch = searchViewModel::startSearch

  val postActions =
    remember(isSearchActive) {
      PostActions(
        context = context,
        uriHandler = uriHandler,
        markPostAsRead = postsViewModel::markPostAsRead,
        onToggleSave = postsViewModel::toggleSave,
        checkPostRead = postsViewModel::isPostRead,
        checkPostSaved = postsViewModel::isPostSaved,
        loadLinkMetadata = postsViewModel::getLinkMetadata,
      ) { post ->
        navigateToDestination(post.toCommentsDestination(), false)
      }
    }

  LaunchedEffect(deepLinkDestination) {
    if (deepLinkDestination != null) {
      navigateToDestination(deepLinkDestination, true)
      clearDeepLink()
    }
  }

  val assistUri =
    when {
      currentDestinationIsTopLevel && isSearchActive -> SEARCH_WEB_URI
      currentDestination == Hottest -> HOTTEST_WEB_URI
      currentDestination == Newest -> NEWEST_WEB_URI
      currentDestination is Comments -> currentDestination.commentsUrl
      currentDestination is User -> "https://lobste.rs/u/${currentDestination.username}"
      else -> null
    }
  SideEffect { setWebUri(assistUri) }

  LaunchedEffect(currentDestinationIsTopLevel, isSearchActive) {
    if (!currentDestinationIsTopLevel && isSearchActive) {
      dismissSearch()
    }
  }

  LaunchedEffect(searchState.requestGeneration) {
    if (searchState.requestGeneration > 0) {
      searchResultsListState.scrollToItem(0)
    }
  }

  // Let NavDisplay own back-stack pops so predictive back can animate the previous destination.
  BackHandler(enabled = shouldDismissSearchOnBack(isSearchActive, currentDestinationIsTopLevel)) {
    dismissSearch()
  }

  InAppUpdates(snackbarHostState = snackbarHostState)

  @Suppress("UnstableApiUsage")
  SentryNavEffect(
    backStack = backStack,
    backStackEntryMapper = { entry ->
      when (entry) {
        is Hottest -> SentryBackStackEntry("Hottest")
        is Newest -> SentryBackStackEntry("Newest")
        is Saved -> SentryBackStackEntry("Saved")
        is Comments -> SentryBackStackEntry("Comments", mapOf("post_id" to entry.postId))
        is Reply ->
          SentryBackStackEntry(
            "Reply",
            mapOf("replying_to" to entry.commentId, "replying_on" to entry.postId),
          )
        is User -> SentryBackStackEntry("User", mapOf("user" to entry.username))
        is Settings -> SentryBackStackEntry("Settings")
        is Login -> SentryBackStackEntry("Login")
        is AboutLibraries -> SentryBackStackEntry("AboutLibraries")
        is TagFiltering -> SentryBackStackEntry("TagFiltering")
        else -> null
      }
    },
    // These are all defaults, just keeping it here so I can remember how to tweak if the new data
    // looks off.
    options =
      SentryNavOptions {
        enableNavigationBreadcrumbs = true
        enableNavigationTransactions = true
        captureBackStack = true
        maxCapturedBackStackEntries = 5
      },
  )

  Scaffold(
    topBar = {
      ClawAppBar(
        activity = activity,
        isTopLevel = currentDestinationIsTopLevel,
        mode =
          if (currentDestinationIsTopLevel && isSearchActive) {
            ClawTopBarMode.Searching(query = searchQuery, requestFocus = true)
          } else {
            ClawTopBarMode.Browsing
          },
        navigateTo = { destination -> navigateToDestination(destination, false) },
        popBackStack = { popBackStack(backStack) },
        onStartSearch = startSearch,
        onDismissSearch = dismissSearch,
        onQueryChange = searchViewModel::updateQuery,
        onSearch = { query -> executeSearch(query) },
      ) {
        if (
          normalizedSearchQuery.isNotBlank() && normalizedSearchQuery == lastExecutedSearchQuery
        ) {
          SearchResultsList(
            lazyPagingItems = searchResults,
            onClearanceCookie = settingsViewModel::saveAnubisCookie,
          ) { listModifier ->
            NetworkPosts(
              lazyPagingItems = searchResults,
              listState = searchResultsListState,
              postActions = postActions,
              filteredTags = filteredTags,
              contentPadding = PaddingValues(bottom = searchResultsBottomPadding),
              modifier = listModifier,
            )
          }
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
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    Row {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.NAVIGATION_RAIL) {
        ClawNavigationRail(
          items = navItems,
          currentNavKey = currentDestination,
          navigateTo = { destination -> navigateToDestination(destination, false) },
          isVisible = currentDestinationIsTopLevel && !isSearchActive,
        )
      }
      NavigationHost(
        backStack = backStack.toImmutableList(),
        onBack = {
          if (popBackStack(backStack) == null) {
            activity?.finish()
          }
        },
        sceneStrategies = persistentListOf(listDetailStrategy),
        entryDecorators = persistentListOf(rememberSaveableStateHolderNavEntryDecorator()),
        popTransitionSpec = {
          slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
        },
        predictivePopTransitionSpec = {
          slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) togetherWith
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
        },
        contributors =
          persistentListOf(
            postsNavigationEntryContributor(
              hottestPosts = postsViewModel.hottestPosts,
              newestPosts = postsViewModel.newestPosts,
              hottestListState = hottestListState,
              newestListState = newestListState,
              savedPosts = savedPosts,
              savedListState = savedListState,
              postActions = postActions,
              filteredTags = filteredTags,
              contentPadding = contentPadding,
            ),
            discussionNavigationEntryContributor(
              postActions =
                remember(isSearchActive) {
                  PostActions(
                    context = context,
                    uriHandler = uriHandler,
                    markPostAsRead = postsViewModel::markPostAsRead,
                    onToggleSave = postsViewModel::toggleSave,
                    checkPostRead = postsViewModel::isPostRead,
                    checkPostSaved = postsViewModel::isPostSaved,
                    loadLinkMetadata = postsViewModel::getLinkMetadata,
                  ) { post ->
                    navigateToDestination(post.toCommentsDestination(), true)
                  }
                },
              contentPadding = contentPadding,
              openUserProfile = { navigateToDestination(User(it), false) },
              navigate = { destination, allowStacking ->
                navigateToDestination(destination, allowStacking)
              },
              onReplySubmitted = { popBackStack(backStack) },
            ),
            profileNavigationEntryContributor(
              contentPadding = contentPadding,
              metadata =
                NavDisplay.transitionSpec {
                  slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(200),
                  ) togetherWith ExitTransition.KeepUntilTransitionsFinished
                } +
                  NavDisplay.popTransitionSpec {
                    EnterTransition.None togetherWith
                      slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
                  } +
                  NavDisplay.predictivePopTransitionSpec {
                    EnterTransition.None togetherWith
                      slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
                  } +
                  ListDetailSceneStrategy.extraPane(),
              navigate = { destination, allowStacking ->
                navigateToDestination(destination, allowStacking)
              },
            ),
            settingsNavigationEntryContributor(
              metadata = ListDetailSceneStrategy.extraPane(),
              contentPadding = contentPadding,
              openLibrariesScreen = { navigateToDestination(AboutLibraries, false) },
              openRepository = {
                uriHandler.openUri("https://github.com/msfjarvis/compose-lobsters")
              },
              openTagFiltering = { navigateToDestination(it, false) },
              openLoginScreen = { navigateToDestination(Login, false) },
              onLogout = settingsViewModel::logout,
              isLoggedIn = isLoggedIn,
              username = username,
              snackbarHostState = snackbarHostState,
              importPosts = settingsViewModel::importPosts,
              exportPostsAsJson = settingsViewModel::exportPostsAsJson,
              savedPostsCount = savedPostsCount,
              isDailySavedPostReminderEnabled = isDailySavedPostReminderEnabled,
              dailySavedPostReminderTime = dailySavedPostReminderTime,
              onDailySavedPostReminderEnabledChange = onDailySavedPostReminderEnabledChange,
              onDailySavedPostReminderTimeChange = settingsViewModel::setDailySavedPostReminderTime,
              settingsAppConfig = settingsViewModel.appConfig,
              librariesContent = { padding, contentModifier ->
                LibrariesContainer(
                  libraries = libraries,
                  contentPadding = padding,
                  modifier = contentModifier,
                )
              },
            ),
            loginNavigationEntryContributor(
              onLoginSuccess = settingsViewModel::saveCookie,
              popBackStack = { popBackStack(backStack) },
              metadata = ListDetailSceneStrategy.extraPane(),
            ),
            NavigationEntryContributor {
              entry<TagFiltering> {
                TagList(
                  popBackStack = { popBackStack(backStack) },
                  contentPadding = contentPadding,
                )
              }
            },
          ),
      )
    }
  }
}

private fun UIPost.toCommentsDestination() =
  Comments(
    postId = shortId,
    commentsUrl = commentsUrl,
    header = CommentsHeaderData(shortId, commentsUrl, title, tags, url, submitter, userIsAuthor),
  )

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun navigateTo(
  backStack: MutableList<NavKey>,
  destination: NavKey,
  allowStacking: Boolean = false,
) {
  BackStackNavigator(backStack, Hottest).navigate(destination, allowStacking)
}

private fun popBackStack(backStack: MutableList<NavKey>): NavKey? =
  BackStackNavigator(backStack, Hottest).pop()
