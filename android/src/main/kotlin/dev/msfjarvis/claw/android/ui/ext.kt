/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.content.Intent
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

fun PostActions(
  context: Context,
  urlLauncher: UrlLauncher,
  viewModel: ClawViewModel,
  navigateToComments: (String) -> Unit,
): PostActions {
  return object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {
      viewModel.markPostAsRead(postId)
      urlLauncher.openUri(postUrl.ifEmpty { commentsUrl })
    }

    override fun viewComments(postId: String) {
      viewModel.markPostAsRead(postId)
      navigateToComments(postId)
    }

    override fun viewCommentsPage(post: UIPost) {
      urlLauncher.openUri(post.commentsUrl)
    }

    override fun toggleSave(post: UIPost) {
      viewModel.toggleSave(post)
    }

    override fun share(post: UIPost) {
      val sendIntent: Intent =
        Intent().apply {
          action = Intent.ACTION_SEND
          putExtra(Intent.EXTRA_TEXT, post.url.ifEmpty { post.commentsUrl })
          putExtra(Intent.EXTRA_TITLE, post.title)
          type = "text/plain"
        }
      val shareIntent = Intent.createChooser(sendIntent, null)
      context.startActivity(shareIntent)
    }

    override fun isPostRead(post: UIPost): Boolean = viewModel.isPostRead(post)

    override fun isPostSaved(post: UIPost): Boolean = viewModel.isPostSaved(post)

    override suspend fun getComments(postId: String): UIPost {
      return viewModel.getPostComments(postId)
    }

    override suspend fun getLinkMetadata(url: String): LinkMetadata {
      return viewModel.getLinkMetadata(url)
    }
  }
}
