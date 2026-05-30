/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.model.ReplyForm
import dev.zacsweers.metro.Inject
import java.io.IOException
import okhttp3.MultipartBody

private const val XML_HTTP_REQUEST = "XMLHttpRequest"
private const val XML_HTTP_REQUEST_DUPLICATE = "XMLHttpRequest, XMLHttpRequest"
private const val ACCEPT_ANY = "*/*"

@Inject
class AuthenticatedLobstersApi(private val api: LobstersApi) {

  suspend fun upvoteComment(commentId: String): ApiResult<Unit, Unit> {
    return withCSRFToken { csrfToken ->
      api.upvoteComment(commentId, csrfToken, XML_HTTP_REQUEST, part("reason", ""))
    }
  }

  suspend fun unvoteComment(commentId: String): ApiResult<Unit, Unit> {
    return withCSRFToken { csrfToken ->
      api.unvoteComment(commentId, csrfToken, XML_HTTP_REQUEST, part("reason", ""))
    }
  }

  suspend fun reply(commentId: String, storyId: String, comment: String): ApiResult<Unit, Unit> {
    val referer = "${LobstersApi.BASE_URL}/s/$storyId"
    return withCSRFToken { csrfToken ->
      when (val formResponse = api.getReplyForm(commentId, csrfToken, XML_HTTP_REQUEST, referer)) {
        is Success -> {
          val form = formResponse.value
          if (!form.isValid()) {
            ApiResult.unknownFailure(IOException("Lobsters reply form was missing required fields"))
          } else {

            api.postReply(
              csrfToken = csrfToken,
              requestedWith = XML_HTTP_REQUEST_DUPLICATE,
              referer = referer,
              origin = LobstersApi.BASE_URL,
              accept = ACCEPT_ANY,
              authenticityToken = part("authenticity_token", form.authenticityToken),
              storyId = part("story_id", form.storyId),
              method = part("_method", form.method),
              parentCommentShortId = part("parent_comment_short_id", form.parentCommentShortId),
              comment = part("comment", comment),
              commit = part("commit", "Post"),
            )
          }
        }
        is Failure -> formResponse.toUnitFailure()
      }
    }
  }

  private suspend fun withCSRFToken(
    block: suspend (String) -> ApiResult<Unit, Unit>
  ): ApiResult<Unit, Unit> {
    return when (val token = api.getCSRFToken()) {
      is Success -> {
        val value = token.value.value
        if (value.isBlank()) {
          ApiResult.unknownFailure(IOException("Lobsters CSRF token was empty"))
        } else {
          block(value)
        }
      }
      is Failure -> token.toUnitFailure()
    }
  }

  private fun ReplyForm.isValid(): Boolean =
    authenticityToken.isNotBlank() &&
      storyId.isNotBlank() &&
      method.isNotBlank() &&
      parentCommentShortId.isNotBlank()

  private fun part(name: String, value: String): MultipartBody.Part =
    MultipartBody.Part.createFormData(name, value)

  private fun Failure<*>.toUnitFailure(): Failure<Unit> =
    when (this) {
      is Failure.ApiFailure -> ApiResult.apiFailure(error as? Unit)
      is Failure.HttpFailure -> ApiResult.httpFailure(code, error as? Unit)
      is Failure.NetworkFailure -> ApiResult.networkFailure(error)
      is Failure.UnknownFailure -> ApiResult.unknownFailure(error)
    }
}
