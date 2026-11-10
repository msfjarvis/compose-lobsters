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
  fun `search parses story results from HTML`() = runTest {
    val posts = api.searchPosts("ChatGPT", 1)
    assertIs<ApiResult.Success<List<LobstersPost>>>(posts)
    assertThat(posts.value).isNotEmpty()
    assertThat(posts.value.first().shortId).isEqualTo("1kesjm")
    assertThat(posts.value.first().title).isEqualTo("Abolish AI! Manifesto")
    assertThat(posts.value.first().url).isEqualTo("https://abolish-ai.net/abolish-ai-a-manifesto/")
  }
}
