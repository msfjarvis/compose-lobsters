package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult.Success
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.runBlocking

class ApiTest {
  private val wrapper = ApiWrapper(newEitherNetController())
  private val api
    get() = wrapper.api

  @Test
  fun `api gets correct number of items`() = runBlocking {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertEquals(25, posts.value.size)
  }

  @Test
  fun `posts with no urls`() = runBlocking {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    val commentsOnlyPosts = posts.value.asSequence().filter { it.url.isEmpty() }.toSet()
    assertEquals(2, commentsOnlyPosts.size)
  }

  @Test
  fun `post details with comments`() = runBlocking {
    val postDetails = api.getPostDetails("tdfoqh")
    assertIs<Success<LobstersPostDetails>>(postDetails)
    assertEquals(7, postDetails.value.comments.size)
  }

  @Test
  fun `get user details`() = runBlocking {
    val user = api.getUser("msfjarvis")
    assertIs<Success<User>>(user)
    assertEquals("msfjarvis", user.value.username)
  }
}
