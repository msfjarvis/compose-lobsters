/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import com.slack.eithernet.ApiResult.Failure
import com.slack.eithernet.ApiResult.Success
import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.FiltersPage
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.jupiter.api.Test

class AuthenticatedLobstersApiTest {
  private val wrapper = ApiWrapper()
  private val authenticatedApi = wrapper.authenticatedApi

  @Test
  fun `upvote comment succeeds`() = runTest {
    val result = authenticatedApi.upvoteComment("0unzyg")

    assertIs<Success<Unit>>(result)
  }

  @Test
  fun `unvote comment succeeds`() = runTest {
    val result = authenticatedApi.unvoteComment("0unzyg")

    assertIs<Success<Unit>>(result)
  }

  @Test
  fun `reply succeeds`() = runTest {
    val result = authenticatedApi.reply("edtrox", "znlkib", "Thanks for the details!")

    assertIs<Success<Unit>>(result)
  }

  @Test
  fun `save blocked tags posts full permanent set and returns canonical refreshed set`() = runTest {
    val api =
      RecordingFiltersLobstersApi(
        filtersResponses =
          ArrayDeque(
            listOf(
              FiltersPage(
                authenticityToken = "filters-token",
                tags = emptyList(),
                blockedTags = setOf("existing"),
              ),
              FiltersPage(
                authenticityToken = "filters-token",
                tags = emptyList(),
                blockedTags = setOf("android", "kotlin"),
              ),
            )
          )
      )
    val authenticatedApi = AuthenticatedLobstersApi(api)

    val result = authenticatedApi.saveBlockedTags(setOf("kotlin", "android"))

    assertThat(api.savedAuthenticityToken).isEqualTo("filters-token")
    assertThat(api.savedTags).containsExactly("tags[android]", "1", "tags[kotlin]", "1")
    assertThat(api.savedCommit).isEqualTo("Save Filters")
    assertIs<Success<Set<String>>>(result)
    assertThat(result.value).containsExactly("android", "kotlin")
  }

  @Test
  fun `save blocked tags fails fast when filters token is blank`() = runTest {
    val api =
      RecordingFiltersLobstersApi(
        filtersResponses =
          ArrayDeque(
            listOf(
              FiltersPage(authenticityToken = "", tags = emptyList(), blockedTags = emptySet())
            )
          )
      )
    val authenticatedApi = AuthenticatedLobstersApi(api)

    val result = authenticatedApi.saveBlockedTags(setOf("kotlin"))

    assertIs<Failure.UnknownFailure>(result)
    assertThat(result.error).hasMessageThat().isEqualTo("Lobsters filters page token was empty")
    assertThat(api.saveFiltersCallCount).isEqualTo(0)
  }

  @Test
  fun `save blocked tags re-reads filters after save`() = runTest {
    val api =
      RecordingFiltersLobstersApi(
        filtersResponses =
          ArrayDeque(
            listOf(
              FiltersPage(
                authenticityToken = "filters-token",
                tags = emptyList(),
                blockedTags = setOf("old"),
              ),
              FiltersPage(
                authenticityToken = "filters-token",
                tags = emptyList(),
                blockedTags = setOf("canonical"),
              ),
            )
          )
      )
    val authenticatedApi = AuthenticatedLobstersApi(api)

    val result = authenticatedApi.saveBlockedTags(setOf("draft"))

    assertThat(api.getFiltersCallCount).isEqualTo(2)
    assertIs<Success<Set<String>>>(result)
    assertThat(result.value).containsExactly("canonical")
  }
}

private class RecordingFiltersLobstersApi(
  private val filtersResponses: ArrayDeque<FiltersPage>,
  private val saveFiltersResult: ApiResult<Unit, Unit> = ApiResult.success(Unit),
) : LobstersApi {
  var getFiltersCallCount: Int = 0
    private set

  var saveFiltersCallCount: Int = 0
    private set

  var savedAuthenticityToken: String? = null
    private set

  var savedTags: Map<String, String>? = null
    private set

  var savedCommit: String? = null
    private set

  override suspend fun getHottestPosts(page: Int): ApiResult<List<LobstersPost>, Unit> =
    error("unused")

  override suspend fun getNewestPosts(page: Int): ApiResult<List<LobstersPost>, Unit> =
    error("unused")

  override suspend fun getPostDetails(postId: String): ApiResult<LobstersPostDetails, Unit> =
    error("unused")

  override suspend fun getUser(username: String): ApiResult<User, Unit> = error("unused")

  override suspend fun getCSRFToken(): ApiResult<CSRFToken, Unit> = error("unused")

  override suspend fun getTags(): ApiResult<List<Tag>, Unit> = error("unused")

  override suspend fun getFilters(): ApiResult<FiltersPage, Unit> {
    getFiltersCallCount += 1
    return ApiResult.success(filtersResponses.removeFirst())
  }

  override suspend fun saveFilters(
    authenticityToken: String,
    tags: Map<String, String>,
    commit: String,
  ): ApiResult<Unit, Unit> {
    saveFiltersCallCount += 1
    savedAuthenticityToken = authenticityToken
    savedTags = tags
    savedCommit = commit
    return saveFiltersResult
  }

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
  ): ApiResult<ReplyForm, Unit> = error("unused")

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
  ): ApiResult<Unit, Unit> = error("unused")
}
