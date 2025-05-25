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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.msfjarvis.claw.android.MainActivity
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationBar
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.ClawNavigationType
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Destination
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nav3Screen(
  urlLauncher: UrlLauncher,
  windowSizeClass: WindowSizeClass,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val backStack = rememberNavBackStack<Destination>(Hottest)

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
      backStack.add(Comments(postIdOverride))
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
  val navDestinations = navItems.map(NavigationItem::destination).toPersistentList()
  // endregion

  val postActions = remember {
    PostActions(context, urlLauncher, viewModel) { backStack.add(Comments(it)) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        modifier = Modifier.shadow(8.dp),
        navigationIcon = {
          if (backStack.firstOrNull() !in navDestinations) {
            IconButton(onClick = { if (backStack.removeLastOrNull() == null) activity?.finish() }) {
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
          if (backStack.firstOrNull() in navDestinations) {
            Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
          }
        },
        actions = {
          if (backStack.firstOrNull() in navDestinations) {
            IconButton(onClick = { backStack.add(Search) }) {
              Icon(imageVector = Icons.Filled.Search, contentDescription = "Search posts")
            }
            IconButton(onClick = { backStack.add(Settings) }) {
              Icon(imageVector = Icons.Filled.Tune, contentDescription = "Settings")
            }
          }
        },
      )
    },
    bottomBar = {
      AnimatedVisibility(visible = navigationType == ClawNavigationType.BOTTOM_NAVIGATION) {
        ClawNavigationBar(
          backStack,
          items = navItems,
          isVisible = backStack.firstOrNull() in navDestinations,
          hazeState = hazeState,
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.semantics { testTagsAsResourceId = true },
  ) { contentPadding ->
    NavDisplay(
      backStack = backStack,
      modifier = modifier.hazeSource(hazeState),
      onBack = { backStack.removeLastOrNull() },
      predictivePopTransitionSpec = {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(200)) togetherWith
          slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(200))
      },
      entryProvider =
        entryProvider {
          entry<Hottest> {
            setWebUri("https://lobste.rs/")
            NetworkPosts(
              lazyPagingItems = hottestPosts,
              listState = hottestListState,
              postActions = postActions,
              contentPadding = contentPadding,
            )
          }
          entry<Newest> {
            setWebUri("https://lobste.rs/")
            NetworkPosts(
              lazyPagingItems = newestPosts,
              listState = newestListState,
              postActions = postActions,
              contentPadding = contentPadding,
            )
          }
          entry<Saved> {
            setWebUri(null)
            DatabasePosts(
              items = savedPosts,
              listState = savedListState,
              postActions = postActions,
              contentPadding = contentPadding,
            )
          }
          entry<Settings> {
            SettingsScreen(
              openInputStream = context.contentResolver::openInputStream,
              openOutputStream = context.contentResolver::openOutputStream,
              openLibrariesScreen = { backStack.add(AboutLibraries) },
              importPosts = viewModel::importPosts,
              exportPostsAsJson = viewModel::exportPostsAsJson,
              exportPostsAsHtml = viewModel::exportPostsAsHtml,
              snackbarHostState = snackbarHostState,
              contentPadding = contentPadding,
              modifier = Modifier.fillMaxSize(),
            )
          }
          entry<Comments> { dest ->
            CommentsPage(
              postId = dest.postId,
              postActions = postActions,
              getSeenComments = viewModel::getSeenComments,
              markSeenComments = viewModel::markSeenComments,
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
                }
          ) { dest ->
            UserProfile(
              username = dest.username,
              getProfile = viewModel::getUserProfile,
              contentPadding = contentPadding,
              openUserProfile = { backStack.add(User(it)) },
            )
          }
          entry<Search> {
            setWebUri("https://lobste.rs/search")
            SearchScreen(
              viewModel = viewModel,
              postActions = postActions,
              contentPadding = contentPadding,
            )
          }
          entry<AboutLibraries> {
            LibrariesContainer(contentPadding = contentPadding, modifier = Modifier.fillMaxSize())
          }
        },
    )
  }
}
