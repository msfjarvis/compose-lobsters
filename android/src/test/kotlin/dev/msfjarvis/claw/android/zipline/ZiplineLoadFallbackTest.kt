/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.zipline

import app.cash.zipline.loader.DefaultFreshnessCheckerNotFresh
import app.cash.zipline.loader.FreshnessChecker
import app.cash.zipline.loader.LoadResult
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZiplineLoadFallbackTest {

  @Test
  fun `retries from embedded assets after a network failure`() = runTest {
    val attemptedFreshnessCheckers = mutableListOf<FreshnessChecker>()
    val embeddedFailure =
      LoadResult.Failure(IllegalStateException("embedded bundle is unavailable"))

    val result =
      loadWithEmbeddedFallback(DefaultFreshnessCheckerNotFresh) { freshnessChecker ->
        attemptedFreshnessCheckers += freshnessChecker
        if (freshnessChecker === DefaultFreshnessCheckerNotFresh) {
          LoadResult.Failure(IOException("manifest server is inaccessible"))
        } else {
          embeddedFailure
        }
      }

    assertEquals(embeddedFailure, result)
    assertThat(attemptedFreshnessCheckers)
      .containsExactly(DefaultFreshnessCheckerNotFresh, EmbeddedAssetsFreshnessChecker)
      .inOrder()
  }
}
