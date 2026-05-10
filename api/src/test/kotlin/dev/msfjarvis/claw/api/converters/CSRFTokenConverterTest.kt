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
        "RPe1m3JaXAq0ti7t15hNixpLvmgJ793Ti6y4fOpxtfenyXQK3WU19m7MJb8RGoIf-tC3C9Ilwb8pv0WJUWLbNA"
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
