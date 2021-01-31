package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.injection.ApiModule
import dev.msfjarvis.lobsters.injection.MoshiModule
import dev.msfjarvis.lobsters.util.TestUtils
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class LobstersApiTest {

  companion object {
    private val webServer = MockWebServer()
    private val apiData = TestUtils.getJson("hottest.json")
    private val okHttp = ApiModule.provideClient()
    private val retrofit = ApiModule.provideRetrofit(
      { okHttp },
      { MoshiModule.provideMoshi() },
      "http://localhost:8080/"
    )
    private val apiClient = ApiModule.provideApi(retrofit)

    @JvmStatic
    @BeforeClass
    fun setUp() {
      webServer.start(8080)
      webServer.dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          return MockResponse().setBody(apiData).setResponseCode(200)
        }
      }
    }

    @JvmStatic
    @AfterClass
    fun tearDown() {
      webServer.shutdown()
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
      .filter { it.submitter_user.isModerator }
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
}
