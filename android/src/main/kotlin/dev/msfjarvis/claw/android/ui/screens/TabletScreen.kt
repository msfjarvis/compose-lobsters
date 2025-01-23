/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package dev.msfjarvis.claw.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.R
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.decorations.ClawNavigationRail
import dev.msfjarvis.claw.android.ui.decorations.NavigationItem
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch

private fun ThreePaneScaffoldNavigator<*>.isListExpanded() =
  scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

private fun ThreePaneScaffoldNavigator<*>.isDetailExpanded() =
  scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletScreen(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val context = LocalContext.current
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val navController = rememberNavController()
  val coroutineScope = rememberCoroutineScope()
  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())
  val navigator = rememberListDetailPaneScaffoldNavigator<Comments>()
  val backBehavior =
    if (navigator.isListExpanded() && navigator.isDetailExpanded()) {
      BackNavigationBehavior.PopUntilContentChange
    } else {
      BackNavigationBehavior.PopUntilScaffoldValueChange
    }

  val postActions = remember {
    PostActions(context, urlLauncher, viewModel) {
      coroutineScope.launch {
        navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail, contentKey = Comments(it))
      }
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
        coroutineScope.launch {
          if (savedPosts.isNotEmpty()) savedListState.animateScrollToItem(index = 0)
        }
      },
    )

  BackHandler(navigator.canNavigateBack(backBehavior)) {
    coroutineScope.launch { navigator.navigateBack(backBehavior) }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        navigationIcon = {
          if (navigator.canNavigateBack(backBehavior)) {
            IconButton(
              onClick = { coroutineScope.launch { navigator.navigateBack(backBehavior) } }
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
          if (!navigator.canNavigateBack(backBehavior)) {
            Text(text = stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
          }
        },
      )
    },
    content = { paddingValues ->
      Row {
        ClawNavigationRail(navController = navController, items = navItems, isVisible = true)
        ListDetailPaneScaffold(
          modifier = modifier.padding(paddingValues),
          directive = navigator.scaffoldDirective,
          value = navigator.scaffoldValue,
          listPane = {
            AnimatedPane {
              NavHost(
                navController = navController,
                startDestination = Hottest,
                enterTransition = { fadeIn(tween(350)) },
                exitTransition = { fadeOut(tween(350)) },
              ) {
                composable<Hottest> {
                  NetworkPosts(
                    lazyPagingItems = hottestPosts,
                    listState = hottestListState,
                    postActions = postActions,
                    contentPadding = PaddingValues(),
                  )
                }
                composable<Newest> {
                  NetworkPosts(
                    lazyPagingItems = newestPosts,
                    listState = newestListState,
                    postActions = postActions,
                    contentPadding = PaddingValues(),
                  )
                }
                composable<Saved> {
                  DatabasePosts(
                    items = savedPosts,
                    listState = savedListState,
                    postActions = postActions,
                    contentPadding = PaddingValues(),
                  )
                }
              }
            }
          },
          detailPane = {
            AnimatedPane {
              when (val contentKey = navigator.currentDestination?.contentKey) {
                null -> {
                  Box(Modifier.fillMaxSize()) {
                    Text(
                      text = "Select a post to view comments",
                      modifier = Modifier.align(Alignment.Center),
                    )
                  }
                }
                else -> {
                  CommentsPage(
                    postId = contentKey.postId,
                    postActions = postActions,
                    htmlConverter = htmlConverter,
                    getSeenComments = viewModel::getSeenComments,
                    markSeenComments = viewModel::markSeenComments,
                    openUserProfile = { navController.navigate(User(it)) },
                    contentPadding = PaddingValues(),
                    modifier = Modifier.fillMaxSize(),
                  )
                }
              }
            }
          },
        )
      }
    },
  )
}
