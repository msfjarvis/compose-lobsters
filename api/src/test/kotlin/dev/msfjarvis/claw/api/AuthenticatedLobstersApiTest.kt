/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

import com.slack.eithernet.ApiResult.Success
import com.slack.eithernet.test.newEitherNetController
import dev.msfjarvis.claw.util.TestUtils.assertIs
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AuthenticatedLobstersApiTest {
  private val wrapper = ApiWrapper(newEitherNetController())
  private val authenticatedApi = wrapper.authenticatedApi

  @Test
  fun `upvote comment succeeds`() = runTest {
    val result = authenticatedApi.upvoteComment("0unzyg")

    assertIs<Success<Unit>>(result)
  }

  @Test
  fun `unvote comment succeeds`() = runTest {
    val result = authenticatedApi.unvoteComment("0unzyg")

    assertIs<Success<Unit>>(result)
  }

  @Test
  fun `reply succeeds`() = runTest {
    val result = authenticatedApi.reply("edtrox", "Thanks for the details!")

    assertIs<Success<Unit>>(result)
  }
}
