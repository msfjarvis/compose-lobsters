package dev.msfjarvis.claw.android.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.primarySurface
import androidx.compose.material.rememberScaffoldState
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
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.SavedPost
import io.github.furstenheim.CopyDown

private const val ScrollDelta = 50

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LobstersApp(
  viewModel: ClawViewModel = viewModel(),
  urlLauncher: UrlLauncher,
  setWebUri: (String) -> Unit,
) {
  val copydown = remember { CopyDown() }
  val systemUiController = rememberSystemUiController()
  val scaffoldState = rememberScaffoldState()
  val listState = rememberLazyListState()
  val navController = rememberNavController()
  var currentDestination by remember { mutableStateOf(Destinations.Hottest) }
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
        navController.navigate(Destinations.Comments.format(postId))
      }

      override fun viewCommentsPage(commentsUrl: String) {
        urlLauncher.openUri(commentsUrl)
      }

      override fun toggleSave(post: SavedPost) {
        viewModel.toggleSave(post)
      }
    }
  }
  navController.addOnDestinationChangedListener { _, destination, _ ->
    currentDestination = destination.route ?: Destinations.Hottest
  }
  LobstersTheme(
    darkTheme = isSystemInDarkTheme(),
    providedValues = arrayOf(LocalUriHandler provides urlLauncher),
  ) {
    ProvideWindowInsets {
      val useDarkIcons = MaterialTheme.colors.isLight
      val statusBarColor = MaterialTheme.colors.primarySurface

      SideEffect {
        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = useDarkIcons)
        systemUiController.setNavigationBarColor(color = Color.Transparent)
      }
      val items = viewModel.pagerFlow.collectAsLazyPagingItems()

      Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ClawAppBar(modifier = Modifier.statusBarsPadding()) },
        floatingActionButton = {
          ClawFab(
            isFabVisible = isFabVisible && currentDestination == Destinations.Hottest,
            listState = listState,
            modifier = Modifier.navigationBarsPadding(),
          )
        },
      ) { paddingValues ->
        NavHost(navController, startDestination = Destinations.Hottest) {
          composable(Destinations.Hottest) {
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
          composable(Destinations.Comments.format("{postId}")) { backStackEntry ->
            val postId = requireNotNull(backStackEntry.arguments?.getString("postId"))
            setWebUri("https://lobste.rs/s/$postId")
            CommentsPage(
              postId = postId,
              getDetails = viewModel::getPostComments,
              htmlToMarkdown = { source -> copydown.convert(source) },
              paddingValues = paddingValues,
            )
          }
        }
      }
    }
  }
}
