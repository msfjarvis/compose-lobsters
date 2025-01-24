/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.lists.DatabasePosts
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.android.ui.navigation.AboutLibraries
import dev.msfjarvis.claw.android.ui.navigation.AppDestinations
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Hottest
import dev.msfjarvis.claw.android.ui.navigation.Newest
import dev.msfjarvis.claw.android.ui.navigation.Saved
import dev.msfjarvis.claw.android.ui.navigation.Settings
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile
import kotlinx.collections.immutable.persistentMapOf

@Composable
fun TabletScreen2(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  // TODO: Needs a custom Saver implementation, should probably be an ArrayDeque
  val navigationBackStack = rememberSaveable {
    mutableStateListOf(AppDestinations.HOTTEST.destination)
  }

  val context = LocalContext.current
  val hottestListState = rememberLazyListState()
  val newestListState = rememberLazyListState()
  val savedListState = rememberLazyListState()
  val snackbarHostState = remember { SnackbarHostState() }
  val postActions = remember {
    PostActions(context, urlLauncher, viewModel) { navigationBackStack.add(Comments(it)) }
  }

  val hottestPosts = viewModel.hottestPosts.collectAsLazyPagingItems()
  val newestPosts = viewModel.newestPosts.collectAsLazyPagingItems()
  val savedPosts by viewModel.savedPostsByMonth.collectAsStateWithLifecycle(persistentMapOf())

  BackHandler(navigationBackStack.size > 1) {
    navigationBackStack.removeAt(navigationBackStack.size - 1)
  }

  val contentPadding = PaddingValues()

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      AppDestinations.entries.forEach {
        item(
          icon = { Icon(imageVector = it.icon, contentDescription = it.label) },
          selected = it.destination == navigationBackStack.first(),
          onClick = { navigationBackStack.add(it.destination) },
        )
      }
    },
    modifier = modifier,
  ) {
    when (navigationBackStack.first()) {
      AboutLibraries -> {
        LibrariesContainer(contentPadding = contentPadding, modifier = Modifier.fillMaxSize())
      }
      Hottest -> {
        setWebUri("https://lobste.rs/")
        NetworkPosts(
          lazyPagingItems = hottestPosts,
          listState = hottestListState,
          postActions = postActions,
          contentPadding = contentPadding,
        )
      }
      Newest -> {
        setWebUri("https://lobste.rs/")
        NetworkPosts(
          lazyPagingItems = newestPosts,
          listState = newestListState,
          postActions = postActions,
          contentPadding = contentPadding,
        )
      }
      Saved -> {
        setWebUri(null)
        DatabasePosts(
          items = savedPosts,
          listState = savedListState,
          postActions = postActions,
          contentPadding = contentPadding,
        )
      }
      Settings -> {
        setWebUri(null)
        SettingsScreen(
          openInputStream = context.contentResolver::openInputStream,
          openOutputStream = context.contentResolver::openOutputStream,
          openLibrariesScreen = { navigationBackStack.add(AboutLibraries) },
          importPosts = viewModel::importPosts,
          exportPostsAsJson = viewModel::exportPostsAsJson,
          exportPostsAsHtml = viewModel::exportPostsAsHtml,
          snackbarHostState = snackbarHostState,
          contentPadding = contentPadding,
          modifier = Modifier.fillMaxSize(),
        )
      }
      is Comments -> {
        val postId = (navigationBackStack.first() as Comments).postId
        setWebUri("https://lobste.rs/s/$postId")
        CommentsPage(
          postId = postId,
          postActions = postActions,
          htmlConverter = htmlConverter,
          getSeenComments = viewModel::getSeenComments,
          markSeenComments = viewModel::markSeenComments,
          contentPadding = contentPadding,
          openUserProfile = { navigationBackStack.add(User(it)) },
        )
      }
      is User -> {
        val username = (navigationBackStack.first() as User).username
        setWebUri("https://lobste.rs/u/$username")
        UserProfile(
          username = username,
          getProfile = viewModel::getUserProfile,
          contentPadding = contentPadding,
          openUserProfile = { navigationBackStack.add(User(it)) },
        )
      }
      else -> {
        Box(Modifier.fillMaxSize()) {
          Text(
            text = "Unexpected destination: ${navigationBackStack.first()}, please report this bug",
            modifier = Modifier.align(Alignment.Center),
          )
        }
      }
    }
  }
}
