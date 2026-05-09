/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.util.TestUtils.getResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test

class SearchConverterTest {
  @Test
  fun `converter parses search result HTML`() {
    val posts =
      SearchConverter.convert(
        getResource("search_chatgpt_page.html").toResponseBody("text/html".toMediaType())
      )

    assertThat(posts).hasSize(20)
    assertThat(posts)
      .containsAtLeast(
        LobstersPost(
          shortId = "vvky3g",
          createdAt = "",
          title = "2024 State of Rust Survey Results",
          url = "https://blog.rust-lang.org/2025/02/13/2024-State-Of-Rust-Survey-results.html",
          description = "",
          commentCount = 10,
          commentsUrl = "https://lobste.rs/s/vvky3g/2024_state_rust_survey_results",
          submitter = "obsoleszenz",
          userIsAuthor = false,
          tags = listOf("rust"),
        ),
        LobstersPost(
          shortId = "wjoric",
          createdAt = "",
          title = "New hack uses prompt injection to corrupt Gemini’s long-term memory",
          url =
            "https://arstechnica.com/security/2025/02/new-hack-uses-prompt-injection-to-corrupt-geminis-long-term-memory/",
          description = "",
          commentCount = 0,
          commentsUrl = "https://lobste.rs/s/wjoric/new_hack_uses_prompt_injection_corrupt",
          submitter = "laktak",
          userIsAuthor = false,
          tags = listOf("ai"),
        ),
      )
      .inOrder()
  }
}
