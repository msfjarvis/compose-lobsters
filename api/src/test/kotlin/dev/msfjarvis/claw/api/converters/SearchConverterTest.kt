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
      .inOrder()
  }
}
