/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult.Success
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ApiTest {
  private val wrapper = ApiWrapper(newEitherNetController())
  private val api
    get() = wrapper.api

  @Test
  fun `api gets correct number of items`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
  }

  @Test
  fun `posts with no urls`() = runTest {
    val posts = api.getHottestPosts(1)
    assertIs<Success<List<LobstersPost>>>(posts)
    val commentsOnlyPosts = posts.value.asSequence().filter { it.url.isEmpty() }.toSet()
    assertThat(commentsOnlyPosts).hasSize(0)
  }

  @Test
  fun `post details with comments`() = runTest {
    val postDetails = api.getPostDetails("tdfoqh")
    assertIs<Success<LobstersPostDetails>>(postDetails)
    assertThat(postDetails.value.comments).hasSize(10)
  }

  @Test
  fun `get user details`() = runTest {
    val user = api.getUser("msfjarvis")
    assertIs<Success<User>>(user)
    assertThat(user.value.username).isEqualTo("msfjarvis")
  }

  @Test
  fun `retrieve CSRF token`() = runTest {
    val token = api.getCSRFToken()
    assertIs<Success<CSRFToken>>(token)
    assertThat(token.value.value)
      .isEqualTo(
        "RPe1m3JaXAq0ti7t15hNixpLvmgJ793Ti6y4fOpxtfenyXQK3WU19m7MJb8RGoIf-tC3C9Ilwb8pv0WJUWLbNA"
      )
  }

  @Test
  fun `retrieve tags`() = runTest {
    val tags = api.getTags()
    assertIs<Success<List<Tag>>>(tags)
    assertThat(tags.value).isNotEmpty()
    assertThat(tags.value.first().tag).isEqualTo("ruby")
    assertThat(tags.value.first().description).isEqualTo("Ruby programming")
  }
}
