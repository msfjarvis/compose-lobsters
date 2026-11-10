/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.posts.impl

import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.UriHandler
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost

fun PostActions(
  context: Context,
  uriHandler: UriHandler,
  markPostAsRead: (String) -> Unit,
  onToggleSave: (UIPost) -> Unit,
  checkPostRead: (UIPost) -> Boolean,
  checkPostSaved: (UIPost) -> Boolean,
  loadLinkMetadata: suspend (String) -> LinkMetadata,
  navigateToComments: (String, String) -> Unit,
): PostActions {
  return object : PostActions {
    override fun viewPost(postId: String, postUrl: String, commentsUrl: String) {
      markPostAsRead(postId)
      uriHandler.openUri(postUrl.ifEmpty { commentsUrl })
    }

    override fun viewComments(postId: String, commentsUrl: String) {
      markPostAsRead(postId)
      navigateToComments(postId, commentsUrl)
    }

    override fun viewCommentsPage(post: UIPost) {
      uriHandler.openUri(post.commentsUrl)
    }

    override fun toggleSave(post: UIPost) {
      onToggleSave(post)
    }

    override fun share(post: UIPost) {
      shareUrl(post.url.ifEmpty { post.commentsUrl }, post.title)
    }

    override fun shareComment(commentId: String) {
      shareUrl("https://lobste.rs/c/$commentId")
    }

    override fun isPostRead(post: UIPost): Boolean = checkPostRead(post)

    override fun isPostSaved(post: UIPost): Boolean = checkPostSaved(post)

    override suspend fun getLinkMetadata(url: String): LinkMetadata {
      return loadLinkMetadata(url)
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
