/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.util.TestUtils.getResource
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test

class CSRFTokenConverterTest {
  @Test
  fun `converter extracts CSRF token`() {
    val token =
      CSRFTokenConverter.convert(
        getResource("csrf_page.html").toResponseBody("text/html".toMediaType())
      )

    assertThat(token.value)
      .isEqualTo(
        "dvJ8r_CkOImcHQ5ZLUWlJeQVoPEPQ3rK85DNgiZJcehafqwYP8jESW8AhMf0uQGLqqLbsarYiISCghnDaUd6wA"
      )
  }

  @Test
  fun `converter returns empty token when CSRF token is absent`() {
    val token =
      CSRFTokenConverter.convert(
        "<html><head></head><body></body></html>".toResponseBody("text/html".toMediaType())
      )

    assertThat(token.value).isEmpty()
  }
}
