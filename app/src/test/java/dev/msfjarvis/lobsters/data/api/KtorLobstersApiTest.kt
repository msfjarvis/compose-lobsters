package dev.msfjarvis.lobsters.data.api

import dev.msfjarvis.lobsters.util.TestUtils
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.fullPath
import io.ktor.http.headersOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class KtorLobstersApiTest {

  companion object {
    @JvmStatic
    private lateinit var client: HttpClient
    @JvmStatic
    private lateinit var apiClient: LobstersApi

    @JvmStatic
    @BeforeClass
    fun setUp() {
      client = HttpClient(MockEngine) {
        install(JsonFeature) {
          serializer = KotlinxSerializer()
        }
        engine {
          addHandler { request ->
            when (request.url.fullPath) {
              "/hottest.json?page=1" -> {
                val responseHeaders = headersOf("Content-Type" to listOf("application/json"))
                respond(TestUtils.getJson("hottest.json"), headers = responseHeaders)
              }
              else -> error("Unhandled ${request.url.fullPath}")
            }
          }
        }
      }
      apiClient = KtorLobstersApi(client)
    }

    @JvmStatic
    @AfterClass
    fun tearDown() {
      client.close()
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
}
