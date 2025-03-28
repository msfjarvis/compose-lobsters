/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui

import android.content.Context
import android.content.Intent
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.android.viewmodel.ClawViewModel
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.model.LinkMetadata
import dev.msfjarvis.claw.model.UIPost
import java.io.IOException
import java.net.HttpURLConnection

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

/**
 * Convert an [ApiResult.Failure.HttpFailure] to a scoped down error with a more useful user-facing
 * message.
 */
@Suppress("NOTHING_TO_INLINE") // We inline this to eliminate the stacktrace frame.
inline fun <T : Any> ApiResult.Failure.HttpFailure<T>.toError(): Throwable =
  when (code) {
    HttpURLConnection.HTTP_NOT_FOUND -> IOException("Story was removed by moderator")
    HttpURLConnection.HTTP_INTERNAL_ERROR,
    HttpURLConnection.HTTP_BAD_GATEWAY,
    HttpURLConnection.HTTP_UNAVAILABLE -> IOException("It appears lobste.rs is currently down")
    else -> IOException("API returned an invalid response")
  }
