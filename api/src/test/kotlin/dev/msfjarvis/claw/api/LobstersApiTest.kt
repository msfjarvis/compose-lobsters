package dev.msfjarvis.claw.api

import com.squareup.moshi.Moshi
import dev.msfjarvis.claw.util.TestUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.coroutines.runBlocking
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import okhttp3.OkHttpClient
import org.junit.AfterClass
import org.junit.BeforeClass
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class LobstersApiTest {

  companion object {
    private val webServer = MockWebServer()
    private val moshi = Moshi.Builder().build()
    private val okHttp = OkHttpClient.Builder().build()
    private val retrofit =
      Retrofit.Builder()
        .client(okHttp)
        .baseUrl("http://localhost:8080/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val apiClient = retrofit.create<LobstersApi>()

    @JvmStatic
    @BeforeClass
    fun setUp() {
      webServer.start(8080)
      webServer.dispatcher =
        object : Dispatcher() {
          override fun dispatch(request: RecordedRequest): MockResponse {
            val path = requireNotNull(request.path)
            return when {
              path.startsWith("/hottest") ->
                MockResponse().setBody(TestUtils.getJson("hottest.json")).setResponseCode(200)
              path.startsWith("/s/") ->
                MockResponse()
                  .setBody(TestUtils.getJson("post_details_d9ucpe.json"))
                  .setResponseCode(200)
              else -> fail("'$path' unexpected")
            }
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
    val moderatorPosts = posts.asSequence().filter { it.submitter.isModerator }.toSet()
    assertTrue(moderatorPosts.isEmpty())
  }

  @Test
  fun `posts with no urls`() = runBlocking {
    val posts = apiClient.getHottestPosts(1)
    val commentsOnlyPosts = posts.asSequence().filter { it.url.isEmpty() }.toSet()
    assertEquals(2, commentsOnlyPosts.size)
  }

  @Test
  fun `post details with comments`() = runBlocking {
    val postDetails = apiClient.getPostDetails("d9ucpe")
    assertEquals(7, postDetails.commentCount)
    assertEquals(7, postDetails.comments.size)
  }
}
