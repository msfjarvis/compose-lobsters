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

class ReplyFormConverterTest {
  @Test
  fun `converter extracts reply form hidden fields`() {
    val form =
      ReplyFormConverter.convert(
        getResource("reply_form.html").toResponseBody("text/html".toMediaType())
      )

    assertThat(form.authenticityToken)
      .isEqualTo(
        "AI0414bnzi152-mE0JTWEtwq5B0ZhALBW1W7rGiG5zR-sFaJjWzdARXFM7w_DbPQqWNjzh9bufWZbXG39v5T6g"
      )
    assertThat(form.storyId).isEqualTo("znlkib")
    assertThat(form.method).isEqualTo("post")
    assertThat(form.parentCommentShortId).isEqualTo("edtrox")
  }
}
