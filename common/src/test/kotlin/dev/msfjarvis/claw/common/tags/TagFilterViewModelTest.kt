/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.AuthenticatedLobstersApi
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.core.network.SessionCookieStore
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.FiltersPage
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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
class TagFilterViewModelTest {
  private val dispatcher = StandardTestDispatcher()
  private lateinit var driver: JdbcSqliteDriver
  private lateinit var tagBlocksQueries: TagBlocksQueries

  @BeforeEach
  fun setUp() {
    Dispatchers.setMain(dispatcher)
    driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    tagBlocksQueries = TagBlocksQueries(driver)
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
    driver.close()
  }

  @Test
  fun `chip mutation updates draft without writing repository until save`() =
    runTest(dispatcher) {
      val repository = repository()
      repository.saveTagBlock("existing", null)
      val api =
        FakeTagFiltersApi(
          filtersResponses = ArrayDeque(listOf(filtersPage(blockedTags = setOf("remote"))))
        )
      val viewModel =
        TagFilterViewModel(
          api = api,
          authenticatedApi = AuthenticatedLobstersApi(api),
          tagBlockRepository = repository,
          sessionCookieStore = FakeSessionCookieStore(username = null),
          ioDispatcher = dispatcher,
        )
      advanceUntilIdle()

      viewModel.saveTagBlock("draft", null)
      advanceUntilIdle()

      assertThat(viewModel.filteredTags.value).containsExactly("draft", "existing")
      assertThat(viewModel.tagBlocks.value.map { it.tag }).containsExactly("draft", "existing")
      assertThat(repository.getTagBlocks().first().map { it.tag }).containsExactly("existing")
      assertThat(viewModel.isDirty).isTrue()

      viewModel.save()
      advanceUntilIdle()

      assertThat(repository.getTagBlocks().first().map { it.tag })
        .containsExactly("draft", "existing")
      assertThat(viewModel.isDirty).isFalse()
    }

  @Test
  fun `permanent block can be changed to temporary in draft`() =
    runTest(dispatcher) {
      val repository = repository()
      repository.saveTagBlock("kotlin", null)
      val api = FakeTagFiltersApi(filtersResponses = ArrayDeque(listOf(filtersPage())))
      val viewModel = viewModel(repository, api, username = null)
      advanceUntilIdle()

      val expiration = 1_700_000_000_000L
      viewModel.saveTagBlock("kotlin", expiration)

      assertThat(viewModel.tagBlocks.value.single().expirationMillis).isEqualTo(expiration)
      assertThat(repository.getTagBlocks().first().single().isPermanent).isTrue()
    }

  @Test
  fun `temporary block can be changed to permanent in draft`() =
    runTest(dispatcher) {
      val repository = repository()
      repository.saveTagBlock("kotlin", 1_700_000_000_000L)
      val api = FakeTagFiltersApi(filtersResponses = ArrayDeque(listOf(filtersPage())))
      val viewModel = viewModel(repository, api, username = null)
      advanceUntilIdle()

      viewModel.saveTagBlock("kotlin", null)

      assertThat(viewModel.tagBlocks.value.single().isPermanent).isTrue()
      assertThat(repository.getTagBlocks().first().single().isPermanent).isFalse()
    }

  @Test
  fun `authenticated load overlays remote permanent rows and keeps non conflicting local temporary rows`() =
    runTest(dispatcher) {
      val repository = repository()
      repository.saveTagBlock("android", 1_700_000_000_000L)
      repository.saveTagBlock("conflict", 1_800_000_000_000L)
      val api =
        FakeTagFiltersApi(
          filtersResponses =
            ArrayDeque(listOf(filtersPage(blockedTags = setOf("kotlin", "conflict"))))
        )

      val viewModel = viewModel(repository, api, username = "msfjarvis")
      advanceUntilIdle()

      assertThat(viewModel.filteredTags.value).containsExactly("android", "conflict", "kotlin")
      assertThat(viewModel.tagBlocks.value.map { it.tag to it.expirationMillis })
        .containsExactly(
          "android" to 1_700_000_000_000L,
          "conflict" to null,
          "kotlin" to null,
        )
      assertThat(repository.getTagBlocks().first().map { it.tag to it.expirationMillis })
        .containsExactly(
          "android" to 1_700_000_000_000L,
          "conflict" to null,
          "kotlin" to null,
        )
    }

  @Test
  fun `logged out load ignores checked remote rows`() =
    runTest(dispatcher) {
      val repository = repository()
      repository.saveTagBlock("local", null)
      val api =
        FakeTagFiltersApi(
          filtersResponses = ArrayDeque(listOf(filtersPage(blockedTags = setOf("remote"))))
        )

      val viewModel = viewModel(repository, api, username = null)
      advanceUntilIdle()

      assertThat(viewModel.filteredTags.value).containsExactly("local")
      assertThat(repository.getTagBlocks().first().map { it.tag }).containsExactly("local")
    }

  @Test
  fun `authenticated save sends only permanent tags and preserves non conflicting temporary rows locally`() =
    runTest(dispatcher) {
      val repository = repository()
      val api =
        FakeTagFiltersApi(
          filtersResponses =
            ArrayDeque(
              listOf(
                filtersPage(),
                filtersPage(blockedTags = setOf("android", "kotlin")),
                filtersPage(blockedTags = setOf("android", "kotlin")),
              )
            )
        )
      val viewModel = viewModel(repository, api, username = "msfjarvis")
      advanceUntilIdle()

      viewModel.saveTagBlock("android", null)
      viewModel.saveTagBlock("kotlin", null)
      viewModel.saveTagBlock("temporary", 1_900_000_000_000L)
      advanceUntilIdle()

      viewModel.save()
      advanceUntilIdle()

      assertThat(api.savedTags).containsExactly("tags[android]", "1", "tags[kotlin]", "1")
      assertThat(repository.getTagBlocks().first().map { it.tag to it.expirationMillis })
        .containsExactly(
          "android" to null,
          "kotlin" to null,
          "temporary" to 1_900_000_000_000L,
        )
      assertThat(viewModel.isDirty).isFalse()
    }

  @Test
  fun `failed save preserves dirty draft`() =
    runTest(dispatcher) {
      val repository = repository()
      val api =
        FakeTagFiltersApi(
          filtersResponses = ArrayDeque(listOf(filtersPage(), filtersPage())),
          saveFiltersResult = ApiResult.unknownFailure(IOException("boom")),
        )
      val viewModel = viewModel(repository, api, username = "msfjarvis")
      advanceUntilIdle()

      viewModel.saveTagBlock("kotlin", null)
      advanceUntilIdle()
      viewModel.save()
      advanceUntilIdle()

      assertThat(viewModel.isDirty).isTrue()
      assertThat(viewModel.saveError).isEqualTo("boom")
      assertThat(viewModel.filteredTags.value).containsExactly("kotlin")
      assertThat(repository.getTagBlocks().first()).isEmpty()
    }

  private fun repository(): TagBlockRepository =
    TagBlockRepository(tagBlocksQueries, dispatcher, dispatcher)

  private fun viewModel(
    repository: TagBlockRepository,
    api: FakeTagFiltersApi,
    username: String?,
  ): TagFilterViewModel =
    TagFilterViewModel(
      api = api,
      authenticatedApi = AuthenticatedLobstersApi(api),
      tagBlockRepository = repository,
      sessionCookieStore = FakeSessionCookieStore(username = username),
      ioDispatcher = dispatcher,
    )

  private fun filtersPage(
    blockedTags: Set<String> = emptySet(),
    authenticityToken: String = "filters-token",
  ): FiltersPage =
    FiltersPage(
      authenticityToken = authenticityToken,
      tags =
        listOf(
          Tag(tag = "android", description = "Android"),
          Tag(tag = "kotlin", description = "Kotlin"),
        ),
      blockedTags = blockedTags,
    )
}

private class FakeSessionCookieStore(private var username: String?) : SessionCookieStore {
  override fun get(): String? = null

  override fun getUsername(): String? = username

  override fun set(cookie: String, username: String) {
    this.username = username
  }

  override fun clear() {
    username = null
  }

  override fun isLoggedIn(): Flow<Boolean> = flowOf(username != null)

  override fun username(): Flow<String?> = flowOf(username)
}

private class FakeTagFiltersApi(
  private val filtersResponses: ArrayDeque<FiltersPage>,
  private val saveFiltersResult: ApiResult<Unit, Unit> = ApiResult.success(Unit),
) : LobstersApi {
  var savedTags: Map<String, String>? = null
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

  override suspend fun getFilters(): ApiResult<FiltersPage, Unit> =
    ApiResult.success(filtersResponses.removeFirst())

  override suspend fun saveFilters(
    authenticityToken: String,
    tags: Map<String, String>,
    commit: String,
  ): ApiResult<Unit, Unit> {
    savedTags = tags
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
