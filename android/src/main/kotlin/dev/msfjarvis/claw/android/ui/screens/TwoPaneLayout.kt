package dev.msfjarvis.claw.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.adaptive.*
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.ui.Alignment
import androidx.paging.compose.LazyPagingItems
import dev.msfjarvis.claw.android.ui.lists.NetworkPosts
import dev.msfjarvis.claw.common.comments.CommentsPage
import dev.msfjarvis.claw.common.comments.HTMLConverter
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.PostComments
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.UIPost

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun TwoPaneLayout(
  lazyPagingItems: LazyPagingItems<UIPost>,
  postActions: PostActions,
  htmlConverter: HTMLConverter,
  getSeenComments: (String) -> PostComments?,
  markSeenComments: (String, List<Comment>) -> Unit,
  openUserProfile: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val listState = rememberLazyListState()

  // Track the selected postId
  var selectedPostId by remember { mutableStateOf<String?>(null) }

  val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

  NavigableListDetailPaneScaffold(
    modifier = modifier,
    navigator = navigator,
    listPane = {
      NetworkPosts(
        lazyPagingItems = lazyPagingItems,
        listState = listState,
        postActions = postActions,
        contentPadding = PaddingValues(),
        modifier = Modifier.fillMaxSize(),
      ) { postId ->
        selectedPostId = postId
      }
    },
    detailPane = {
      selectedPostId?.let { postId ->
        CommentsPage(
          postId = postId,
          postActions = postActions,
          htmlConverter = htmlConverter,
          getSeenComments = getSeenComments,
          markSeenComments = markSeenComments,
          openUserProfile = openUserProfile,
          contentPadding = PaddingValues(),
          modifier = Modifier.fillMaxSize()
        )
      } ?: Box(Modifier.fillMaxSize()) {
        // Placeholder when no post is selected
        Text(
          text = "Select a post to view comments",
          modifier = Modifier.align(Alignment.Center)
        )
      }
    }
  )
}

