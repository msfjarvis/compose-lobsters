/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.jupiter.api.Test

class AuthenticatedLobstersApiHeaderTest {

  @Test
  fun `reply submission matches browser xhr headers`() = runTest {
    val api = RecordingLobstersApi()
    val authenticatedApi = AuthenticatedLobstersApi(api)

    authenticatedApi.reply("kkiqui", "2tskyy", "hello")

    assertThat(api.replyFormRequestedWith).isEqualTo("XMLHttpRequest")
    assertThat(api.replyFormReferer).isEqualTo("https://lobste.rs/s/2tskyy")
    assertThat(api.postReplyRequestedWith).isEqualTo("XMLHttpRequest, XMLHttpRequest")
    assertThat(api.postReplyReferer).isEqualTo("https://lobste.rs/s/2tskyy")
    assertThat(api.postReplyOrigin).isEqualTo(LobstersApi.BASE_URL)
    assertThat(api.postReplyAccept).isEqualTo("*/*")
  }
}

private class RecordingLobstersApi : LobstersApi {
  var replyFormRequestedWith: String? = null
  var replyFormReferer: String? = null
  var postReplyRequestedWith: String? = null
  var postReplyReferer: String? = null
  var postReplyOrigin: String? = null
  var postReplyAccept: String? = null

  override suspend fun getHottestPosts(page: Int): ApiResult<PostsPage, Unit> = error("unused")

  override suspend fun getNewestPosts(page: Int): ApiResult<PostsPage, Unit> = error("unused")

  override suspend fun getPostDetails(postId: String): ApiResult<LobstersPostDetails, Unit> =
    error("unused")

  override suspend fun getUser(username: String): ApiResult<User, Unit> = error("unused")

  override suspend fun getCSRFToken(): ApiResult<CSRFToken, Unit> =
    ApiResult.success(CSRFToken("csrf-token"))

  override suspend fun getTags(): ApiResult<TagsPage, Unit> = error("unused")

  override suspend fun upvoteComment(
    commentId: String,
    csrfToken: String,
    requestedWith: String,
    reason: MultipartBody.Part,
  ): ApiResult<Unit, Unit> = error("unused")

  override suspend fun unvoteComment(
    commentId: String,
    csrfToken: String,
    requestedWith: String,
    reason: MultipartBody.Part,
  ): ApiResult<Unit, Unit> = error("unused")

  override suspend fun getReplyForm(
    commentId: String,
    csrfToken: String,
    requestedWith: String,
    referer: String,
  ): ApiResult<ReplyForm, Unit> {
    replyFormRequestedWith = requestedWith
    replyFormReferer = referer
    return ApiResult.success(
      ReplyForm(
        authenticityToken = "auth-token",
        storyId = "2tskyy",
        method = "post",
        parentCommentShortId = commentId,
      )
    )
  }

  override suspend fun postReply(
    csrfToken: String,
    requestedWith: String,
    referer: String,
    origin: String,
    accept: String,
    authenticityToken: MultipartBody.Part,
    storyId: MultipartBody.Part,
    method: MultipartBody.Part,
    parentCommentShortId: MultipartBody.Part,
    comment: MultipartBody.Part,
    commit: MultipartBody.Part,
  ): ApiResult<Unit, Unit> {
    postReplyRequestedWith = requestedWith
    postReplyReferer = referer
    postReplyOrigin = origin
    postReplyAccept = accept
    return ApiResult.success(Unit)
  }
}
