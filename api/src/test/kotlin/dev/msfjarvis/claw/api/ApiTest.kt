package dev.msfjarvis.claw.api

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.mock.MockRetrofit
import retrofit2.mock.create

class ApiTest {
  private val retrofit = Retrofit.Builder().baseUrl(LobstersApi.BASE_URL).build()
  private val api = MockRetrofit.Builder(retrofit).build().create<LobstersApi>().let(::FakeApi)

  @Test
  fun `api gets correct number of items`() = runBlocking {
    val posts = api.getHottestPosts(1)
    assertEquals(25, posts.size)
  }

  @Test
  fun `posts with no urls`() = runBlocking {
    val posts = api.getHottestPosts(1)
    val commentsOnlyPosts = posts.asSequence().filter { it.url.isEmpty() }.toSet()
    assertEquals(2, commentsOnlyPosts.size)
  }

  @Test
  fun `post details with comments`() = runBlocking {
    val postDetails = api.getPostDetails("d9ucpe")
    assertEquals(7, postDetails.comments.size)
  }
}
