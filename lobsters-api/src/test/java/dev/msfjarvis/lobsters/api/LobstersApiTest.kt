package dev.msfjarvis.lobsters.api

import dev.msfjarvis.lobsters.model.LobstersPost
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
  fun `api gets correct number of items`() {
    apiClient.getHottestPosts().enqueue(object : Callback<List<dev.msfjarvis.lobsters.model.LobstersPost>> {
      override fun onResponse(
        call: Call<List<dev.msfjarvis.lobsters.model.LobstersPost>>,
        response: Response<List<dev.msfjarvis.lobsters.model.LobstersPost>>
      ) {
        val posts = response.body()
        require(posts != null)
        assertEquals(25, posts.size)
      }

      override fun onFailure(call: Call<List<dev.msfjarvis.lobsters.model.LobstersPost>>, t: Throwable) {
        fail("Call cannot fail in tests")
      }
    })
  }

  @After
  fun tearDown() {
    webServer.shutdown()
  }
}
