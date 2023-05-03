/*
 * Copyright © 2022-2023 Harsh Shandilya.
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
import dev.msfjarvis.claw.model.Tags
import dev.msfjarvis.claw.model.User
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
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
    assertThat(commentsOnlyPosts).hasSize(2)
  }

  @Test
  fun `post details with comments`() = runTest {
    val postDetails = api.getPostDetails("tdfoqh")
    assertIs<Success<LobstersPostDetails>>(postDetails)
    assertThat(postDetails.value.comments).hasSize(7)
  }

  @Test
  fun `get user details`() = runTest {
    val user = api.getUser("msfjarvis")
    assertIs<Success<User>>(user)
    assertThat(user.value.username).isEqualTo("msfjarvis")
  }

  @Test
  fun `get posts by single tag`() = runTest {
    val tags = Tags()
    tags.addTag("meta")
    val posts = api.getPostsByTags(tags, 1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
    assertThat(posts.value[0].tags).contains("meta")
  }

  @Test
  fun `get posts by multiple tags`() = runTest {
    val tags = Tags()
    tags.addTag("programming")
    tags.addTag("rust")
    val posts = api.getPostsByTags(tags, 1)
    assertIs<Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).hasSize(25)
    assertThat(posts.value[0].tags).containsAnyOf("programming", "rust")
  }
}
