/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class GeneratedWidgetPreviewSupportTest {

  @Test
  fun `generated widget preview posts stay lightweight`() {
    val posts = generatedWidgetPreviewPosts()

    assertThat(posts).hasSize(2)
    assertThat(posts.all { it.title.length <= 48 }).isTrue()
    assertThat(posts.all { it.description.isEmpty() }).isTrue()
  }

  @Test
  fun `generated widget preview registration is deferred`() {
    assertThat(generatedWidgetPreviewDelayMinutes()).isGreaterThan(0)
  }
}
