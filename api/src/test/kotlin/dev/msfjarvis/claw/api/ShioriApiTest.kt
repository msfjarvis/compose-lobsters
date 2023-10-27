/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dev.msfjarvis.claw.model.shiori.AuthRequest
import dev.msfjarvis.claw.model.shiori.AuthResponse
import dev.msfjarvis.claw.model.shiori.Bookmark
import dev.msfjarvis.claw.model.shiori.BookmarkRequest
import dev.msfjarvis.claw.model.shiori.EditedBookmark
import dev.msfjarvis.claw.model.shiori.Tag
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import retrofit2.Retrofit
import retrofit2.create

class ShioriApiTest {

  private lateinit var credentials: AuthResponse

  @BeforeEach
  fun setUp() {
    runBlocking { credentials = api.login(AuthRequest(USER, PASSWORD)) }
  }

  @AfterEach
  fun tearDown() {
    runBlocking {
      val ids = api.getBookmarks(credentials.session).bookmarks.map(Bookmark::id)
      if (ids.isNotEmpty()) {
        api.deleteBookmark(credentials.session, ids)
      }
      api.logout(credentials.session)
    }
  }

  @Test
  fun getBookmarks() = runTest {
    val response = api.getBookmarks(credentials.session)
    assertThat(response.page).isEqualTo(1)
    assertThat(response.bookmarks).isEmpty()
  }

  @Test
  fun addBookmark() = runTest {
    val response =
      api.addBookmark(
        credentials.session,
        BookmarkRequest(
          "https://example.com",
          false,
          0,
          emptyList(),
          "Example Domain",
          """
          This domain is for use in illustrative examples in documents.
          You may use this domain in literature without prior coordination or asking for permission.
        """
            .trimIndent(),
        )
      )
    assertThat(response.url).isEqualTo("https://example.com")
    assertThat(response.title).isEqualTo("Example Domain")
    assertThat(response.excerpt)
      .isEqualTo(
        """
        This domain is for use in illustrative examples in documents.
        You may use this domain in literature without prior coordination or asking for permission.
      """
          .trimIndent()
      )
    assertThat(response.id).isAtLeast(0)
  }

  @Test
  fun editBookmark() = runTest {
    val response =
      api.addBookmark(
        credentials.session,
        BookmarkRequest(
          "https://example.com",
          false,
          0,
          emptyList(),
          "Example Domain",
          """
          This domain is for use in illustrative examples in documents.
          You may use this domain in literature without prior coordination or asking for permission.
          """
            .trimIndent(),
        )
      )
    assertThat(response.tags).isEmpty()
    val newBookmark =
      EditedBookmark(
        id = response.id,
        url = response.url,
        title = response.title,
        tags = listOf(Tag("examples")),
      )
    val edited = api.editBookmark(credentials.session, newBookmark)
    assertThat(edited.tags).isNotEmpty()
    assertThat(edited.tags).containsExactly(Tag("examples"))
  }

  @Test
  fun deleteBookmark() = runTest {
    val response =
      api.addBookmark(
        credentials.session,
        BookmarkRequest(
          "https://example.com",
          false,
          0,
          emptyList(),
          "Example Domain",
          """
          This domain is for use in illustrative examples in documents.
          You may use this domain in literature without prior coordination or asking for permission.
          """
            .trimIndent(),
        )
      )
    val count = api.deleteBookmark(credentials.session, listOf(response.id))
    assertThat(count).isEqualTo(1)
  }

  companion object {
    // Default settings for the container
    private const val USER = "shiori"
    private const val PASSWORD = "gopher"

    private val container =
      GenericContainer("ghcr.io/go-shiori/shiori:v1.5.5").withExposedPorts(8080)

    private val json = Json { ignoreUnknownKeys = true }

    // The mapped port can only be obtained after the container has started, so this has to be lazy.
    private val api by
      lazy(LazyThreadSafetyMode.NONE) {
        Retrofit.Builder()
          .baseUrl("http://${container.host}:${container.firstMappedPort}")
          .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
          .build()
          .create<ShioriApi>()
      }

    @JvmStatic
    @BeforeAll
    fun setupContainer() {
      container.start()
    }

    @JvmStatic
    @AfterAll
    fun destroyContainer() {
      container.stop()
    }
  }
}
