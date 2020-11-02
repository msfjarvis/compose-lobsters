package dev.msfjarvis.lobsters.api

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LobstersApiTest {
  private val webServer = MockWebServer()
  private val apiData = TestUtils.getJson("hottest.json")
  private val apiClient = ApiClient.getClient<LobstersApi>("http://localhost:8080")

  @Before
  fun setUp() {
    webServer.start(8080)
    webServer.dispatcher = object : Dispatcher() {
      override fun dispatch(request: RecordedRequest): MockResponse {
        return MockResponse().setBody(apiData).setResponseCode(200)
      }
    }
  }

  @Test
  fun `api gets correct number of items`() = runBlocking {
    val posts = apiClient.getHottestPosts(1)
    assertEquals(25, posts.size)
  }

  @Test
  fun `no moderator posts in test data`() = runBlocking {
    val posts = apiClient.getHottestPosts(1)
    val moderatorPosts = posts.asSequence()
      .filter { it.submitterUser.isModerator }
      .toSet()
    assertTrue(moderatorPosts.isEmpty())
  }

  @Test
  fun `posts with no urls`() = runBlocking {
    val posts = apiClient.getHottestPosts(1)
    val commentsOnlyPosts = posts.asSequence()
      .filter { it.url.isEmpty() }
      .toSet()
    assertEquals(2, commentsOnlyPosts.size)
  }

  @After
  fun tearDown() {
    webServer.shutdown()
  }
}
