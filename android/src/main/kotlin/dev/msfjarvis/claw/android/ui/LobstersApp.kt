package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.comments.LocalHTMLConverter
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.SavedPost

private const val ScrollDelta = 50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobstersApp(
  viewModel: ClawViewModel = viewModel(),
  urlLauncher: UrlLauncher,
  htmlConverter: HTMLConverter,
  setWebUri: (String) -> Unit,
) {
  val systemUiController = rememberSystemUiController()
  val listState = rememberLazyListState()
  val navController = rememberNavController()
  var isFabVisible by remember { mutableStateOf(false) }
  val nestedScrollConnection = remember {
    object : NestedScrollConnection {
      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.y

        if (delta > ScrollDelta) {
          isFabVisible = true
        } else if (delta < -ScrollDelta) {
          isFabVisible = false
        }

        // We didn't consume any offset here so return Offset.Zero
        return Offset.Zero
      }
    }
  }
  val postActions = remember {
    object : PostActions {
      override fun viewPost(postUrl: String, commentsUrl: String) {
        urlLauncher.openUri(postUrl.ifEmpty { commentsUrl })
      }

      override fun viewComments(postId: String) {
        navController.navigate(Destinations.Comments.getRoute(postId))
      }

      override fun viewCommentsPage(commentsUrl: String) {
        urlLauncher.openUri(commentsUrl)
      }

      override fun toggleSave(post: SavedPost) {
        viewModel.toggleSave(post)
      }
    }
  }
  LobstersTheme(
    LocalUriHandler provides urlLauncher,
    LocalHTMLConverter provides htmlConverter,
    colorScheme = decideColorScheme(LocalContext.current),
  ) {
    ProvideWindowInsets {
      val statusBarColor = MaterialTheme.colorScheme.background

      SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor)
        systemUiController.setNavigationBarColor(color = Color.Transparent)
      }
      val items = viewModel.pagerFlow.collectAsLazyPagingItems()

      Scaffold(
        topBar = { ClawAppBar(modifier = Modifier.statusBarsPadding()) },
        floatingActionButton = {
          ClawFab(
            isFabVisible =
              isFabVisible &&
                navController.currentDestination?.route == Destinations.Hottest.getRoute(),
            listState = listState,
            modifier = Modifier.navigationBarsPadding(),
          )
        },
      ) {
        NavHost(navController, startDestination = Destinations.Hottest.getRoute()) {
          composable(Destinations.Hottest.getRoute()) {
            setWebUri("https://lobste.rs/")
            HottestPosts(
              items,
              listState,
              viewModel::isPostSaved,
              viewModel::reloadPosts,
              postActions,
              Modifier.nestedScroll(nestedScrollConnection),
            )
          }
          composable(Destinations.Comments.getRoute("{postId}")) { backStackEntry ->
            val postId = requireNotNull(backStackEntry.arguments?.getString("postId"))
            setWebUri("https://lobste.rs/s/$postId")
            CommentsPage(
              postId = postId,
              getDetails = viewModel::getPostComments,
              modifier = Modifier.navigationBarsPadding(),
            )
          }
        }
      }
    }
  }
}
