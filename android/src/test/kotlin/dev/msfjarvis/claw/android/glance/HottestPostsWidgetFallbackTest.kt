/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class HottestPostsWidgetFallbackTest {
  @Test
  fun `cached posts are returned when loading succeeds`() = runTest {
    val cachedPosts = listOf("cached post")

    assertThat(cachedPostsOrEmpty { cachedPosts }).isEqualTo(cachedPosts)
  }

  @Test
  fun `empty list is returned when cached post read fails`() = runTest {
    assertThat(cachedPostsOrEmpty<List<String>> { throw IOException("cache unavailable") })
      .isEmpty()
  }

  @Test
  fun `cancellation is not converted to an empty list`() = runTest {
    val thrown =
      try {
        cachedPostsOrEmpty<List<String>> { throw CancellationException("cancelled") }
        null
      } catch (cancellation: CancellationException) {
        cancellation
      }

    assertThat(thrown).isInstanceOf(CancellationException::class.java)
  }
}
