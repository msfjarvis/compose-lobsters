/*
 * Copyright © 2023-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deliveryhero.whetstone.compose.injectedViewModel
import dev.msfjarvis.claw.android.ui.lists.SearchList
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.ui.rememberPostActions
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@Composable
fun SearchScreen(
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String?) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ClawViewModel = injectedViewModel(),
) {
  val navController = rememberNavController()
  val postActions = rememberPostActions(urlLauncher, navController, viewModel)
  val listState = rememberLazyListState()
  Scaffold(modifier = modifier) { paddingValues ->
    NavHost(
      navController = navController,
      startDestination = Destinations.Search.route,
      modifier = Modifier.padding(paddingValues),
    ) {
      composable(route = Destinations.Search.route) {
        setWebUri("https://lobste.rs/search")
        SearchList(
          items = viewModel.searchResults,
          listState = listState,
          postActions = postActions,
          searchQuery = viewModel.searchQuery,
          setSearchQuery = { query -> viewModel.searchQuery = query },
        )
      }
      composable(
        route = Destinations.Comments.route,
        arguments = listOf(navArgument("postId") { type = NavType.StringType }),
      ) { backStackEntry ->
        val postId =
          requireNotNull(backStackEntry.arguments?.getString("postId")) {
            "Navigating to ${Destinations.Comments.route} without necessary 'postId' argument"
          }
        setWebUri("https://lobste.rs/s/$postId")
        CommentsPage(
          postId = postId,
          postActions = postActions,
          htmlConverter = htmlConverter,
          getSeenComments = viewModel::getSeenComments,
          markSeenComments = viewModel::markSeenComments,
          openUserProfile = { username: String ->
            navController.navigate(
              Destinations.User.route.replace(Destinations.User.PLACEHOLDER, username)
            )
          },
        )
      }
    }
  }
}
