/*
 * Copyright © 2022-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import dev.msfjarvis.claw.android.ui.navigation.Destinations
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

fun Context.getActivity(): ComponentActivity? {
  return when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
  }
}

@Composable
fun rememberPostActions(
  urlLauncher: UrlLauncher,
  navController: NavController,
  viewModel: ClawViewModel,
): PostActions {
  return remember {
    object : PostActions {
      override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {
        viewModel.markPostAsRead(postId)
        urlLauncher.openUri(postUrl.ifEmpty { commentsUrl })
      }

      override fun viewComments(postId: String) {
        viewModel.markPostAsRead(postId)
        val currentRoute = navController.currentDestination?.route
        val newRoute =
          Destinations.Comments.route.replace(Destinations.Comments.PLACEHOLDER, postId)
        if (currentRoute != Destinations.Comments.route) navController.navigate(newRoute)
      }

      override fun viewCommentsPage(post: UIPost) {
        urlLauncher.openUri(post.commentsUrl)
      }

      override fun toggleSave(post: UIPost) {
        viewModel.toggleSave(post)
      }

      override suspend fun getComments(postId: String): UIPost {
        return viewModel.getPostComments(postId)
      }

      override suspend fun getLinkMetadata(url: String): LinkMetadata {
        return viewModel.getLinkMetadata(url)
      }
    }
  }
}
