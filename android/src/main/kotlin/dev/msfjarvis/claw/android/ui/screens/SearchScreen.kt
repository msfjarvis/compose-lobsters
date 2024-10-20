/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.paging.compose.collectAsLazyPagingItems
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.ui.PostActions
import dev.msfjarvis.claw.android.ui.lists.SearchList
import dev.msfjarvis.claw.android.ui.navigation.Comments
import dev.msfjarvis.claw.android.ui.navigation.Search
import dev.msfjarvis.claw.android.ui.navigation.User
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.common.user.UserProfile

@Composable
fun SearchScreen(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val context = LocalContext.current
  val navController = rememberNavController()
  val postActions = remember { PostActions(context, urlLauncher, navController, viewModel) }
  val listState = rememberLazyListState()
  val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

  val onPostClick: (String) -> Unit = { postId ->
    navController.navigate("comments/$postId")
  }
  Scaffold(modifier = modifier) { contentPadding ->
    NavHost(navController = navController, startDestination = Search) {
      composable<Search> {
        setWebUri("https://lobste.rs/search")
        SearchList(
          lazyPagingItems = searchResults,
          listState = listState,
          postActions = postActions,
          searchQuery = viewModel.searchQuery,
          contentPadding = contentPadding,
          setSearchQuery = { query -> viewModel.searchQuery = query },
          onPostClick = onPostClick
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
          openUserProfile = { navController.navigate(User(it)) },
          contentPadding = contentPadding,
        )
      }
      composable<User> { backStackEntry ->
        val username = backStackEntry.toRoute<User>().username
        setWebUri("https://lobste.rs/u/$username")
        UserProfile(
          username = username,
          getProfile = viewModel::getUserProfile,
          openUserProfile = { navController.navigate(User(it)) },
          contentPadding = contentPadding,
        )
      }
    }
  }
}
