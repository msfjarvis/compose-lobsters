/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.google.common.truth.Truth.assertThat
import com.slack.eithernet.ApiResult
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.User
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
          shortId = "gjlsdg",
          title = "ChatGPT visits the Emacs doctor",
          url = "https://xenodium.com/chatgpt-visits-the-emacs-doctor/",
          createdAt = "",
          commentCount = 3,
          commentsUrl = "https://lobste.rs/s/gjlsdg/chatgpt_visits_emacs_doctor",
          submitter = User("xenodium", "", null, "/avatars/xenodium-16.png", ""),
          tags = listOf("ai", "emacs"),
          description = "",
        ),
        LobstersPost(
          shortId = "astcqf",
          title =
            "Implementing a question-answering system for PDF documents using ChatGPT and Redis",
          url = "https://mstack.nl/blog/20230623-chatgpt-question-pdf-document/",
          createdAt = "",
          commentCount = 0,
          commentsUrl = "https://lobste.rs/s/astcqf/implementing_question_answering_system",
          submitter = User("asteroid", "", null, "/avatars/asteroid-16.png", ""),
          tags = listOf("ai"),
          description = "",
        ),
      )
  }
}
