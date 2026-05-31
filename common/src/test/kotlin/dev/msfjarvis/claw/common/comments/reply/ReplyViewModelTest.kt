/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.AuthenticatedLobstersApi
import dev.msfjarvis.claw.api.CSRFToken
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.api.PostsPage
import dev.msfjarvis.claw.api.ReplyForm
import dev.msfjarvis.claw.api.TagsPage
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReplyViewModelTest {
  private val dispatcher = StandardTestDispatcher()

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `blank submit exposes validation error and skips api`() =
    runTest(dispatcher) {
      val fakeApi = FakeLobstersApi()
      val viewModel =
        ReplyViewModel(
          api = AuthenticatedLobstersApi(fakeApi),
          commentTextHolder = CommentTextHolder(),
          ioDispatcher = dispatcher,
        )

      viewModel.submit(commentId = "c_1", postId = "story_1")
      advanceUntilIdle()

      assertThat(viewModel.uiState.errorMessage).isEqualTo("Reply cannot be blank")
      assertThat(fakeApi.postReplyCallCount).isEqualTo(0)
    }

  @Test
  fun `successful submit emits completion`() =
    runTest(dispatcher) {
      val fakeApi = FakeLobstersApi()
      val viewModel =
        ReplyViewModel(
          api = AuthenticatedLobstersApi(fakeApi),
          commentTextHolder = CommentTextHolder(),
          ioDispatcher = dispatcher,
        )
      viewModel.updateEditor(TextFieldValue("reply body", TextRange("reply body".length)))

      viewModel.submit(commentId = "c_1", postId = "story_1")
      advanceUntilIdle()

      assertThat(viewModel.uiState.isSubmitting).isFalse()
      assertThat(viewModel.uiState.submitSucceeded).isTrue()
      assertThat(fakeApi.postReplyCallCount).isEqualTo(1)
    }

  @Test
  fun `failed submit preserves editor text and exposes error`() =
    runTest(dispatcher) {
      val fakeApi = FakeLobstersApi(postReplyResult = ApiResult.unknownFailure(IOException("boom")))
      val viewModel =
        ReplyViewModel(
          api = AuthenticatedLobstersApi(fakeApi),
          commentTextHolder = CommentTextHolder(),
          ioDispatcher = dispatcher,
        )
      viewModel.updateEditor(TextFieldValue("reply body", TextRange("reply body".length)))

      viewModel.submit(commentId = "c_1", postId = "story_1")
      advanceUntilIdle()

      assertThat(viewModel.uiState.editor.text).isEqualTo("reply body")
      assertThat(viewModel.uiState.errorMessage).contains("boom")
    }
}

private class FakeLobstersApi(
  private val postReplyResult: ApiResult<Unit, Unit> = ApiResult.success(Unit)
) : LobstersApi {
  var postReplyCallCount: Int = 0
    private set

  override suspend fun getHottestPosts(page: Int): ApiResult<PostsPage, Unit> = error("unused")

  override suspend fun getNewestPosts(page: Int): ApiResult<PostsPage, Unit> = error("unused")

  override suspend fun getPostDetails(postId: String): ApiResult<LobstersPostDetails, Unit> =
    error("unused")

  override suspend fun getUser(username: String): ApiResult<User, Unit> = error("unused")

  override suspend fun getCSRFToken(): ApiResult<CSRFToken, Unit> =
    ApiResult.success(CSRFToken("csrf"))

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
    return ApiResult.success(
      ReplyForm(
        authenticityToken = "auth-token",
        storyId = "story-id",
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
    postReplyCallCount += 1
    return postReplyResult
  }
}
