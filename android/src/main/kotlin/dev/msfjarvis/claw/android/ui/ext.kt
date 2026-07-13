/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.UriHandler
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

fun PostActions(
  context: Context,
  uriHandler: UriHandler,
  viewModel: ClawViewModel,
  navigateToComments: (String) -> Unit,
): PostActions {
  return object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {
      viewModel.markPostAsRead(postId)
      uriHandler.openUri(postUrl.ifEmpty { commentsUrl })
    }

    override fun viewComments(postId: String) {
      viewModel.markPostAsRead(postId)
      navigateToComments(postId)
    }

    override fun viewCommentsPage(post: UIPost) {
      uriHandler.openUri(post.commentsUrl)
    }

    override fun toggleSave(post: UIPost) {
      viewModel.toggleSave(post)
    }

    override fun share(post: UIPost) {
      shareUrl(post.url.ifEmpty { post.commentsUrl }, post.title)
    }

    override fun shareComment(commentId: String) {
      shareUrl("https://lobste.rs/c/$commentId")
    }

    override fun isPostRead(post: UIPost): Boolean = viewModel.isPostRead(post)

    override fun isPostSaved(post: UIPost): Boolean = viewModel.isPostSaved(post)

    override suspend fun getLinkMetadata(url: String): LinkMetadata {
      return viewModel.getLinkMetadata(url)
    }

    private fun shareUrl(url: String, title: String? = null) {
      val sendIntent =
        Intent(Intent.ACTION_SEND).apply {
          putExtra(Intent.EXTRA_TEXT, url)
          title?.let { putExtra(Intent.EXTRA_TITLE, it) }
          type = "text/plain"
        }
      context.startActivity(Intent.createChooser(sendIntent, null))
    }
  }
}
