/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchApiTest {
  private val wrapper = SearchApiWrapper(newEitherNetController())
  private val api
    get() = wrapper.api

  @Test
  fun `search is able to parse HTML`() = runTest {
    val posts = api.searchPosts("ChatGPT", 1)
    assertIs<ApiResult.Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).containsAtLeastElementsIn(results).inOrder()
    assertThat(posts.value).hasSize(20)
  }

  private companion object {
    private val results =
      listOf(
        LobstersPost(
          shortId = "kgem4b",
          createdAt = "",
          title = "The social contract of writing",
          url = "https://jola.dev/posts/the-social-contract-of-writing",
          description = "",
          commentCount = 38,
          commentsUrl = "https://lobste.rs/s/kgem4b/social_contract_writing",
          submitter = "joladev",
          userIsAuthor = true,
          tags = listOf("philosophy", "vibecoding"),
        ),
        LobstersPost(
          shortId = "gydtkf",
          createdAt = "",
          title = "AI Resist List",
          url = "https://airesistlist.org/",
          description = "",
          commentCount = 0,
          commentsUrl = "https://lobste.rs/s/gydtkf/ai_resist_list",
          submitter = "chobeat",
          userIsAuthor = false,
          tags = listOf("ai"),
        ),
      )
  }
}
