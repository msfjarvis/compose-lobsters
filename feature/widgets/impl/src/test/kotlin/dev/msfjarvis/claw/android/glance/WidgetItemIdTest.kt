/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.glance

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class WidgetItemIdTest {
  @Test
  fun `post IDs are stable across reorder and content updates`() {
    val shortIds = listOf("sxlf4a", "widget-preview-1", "widget-preview-2")
    val idsBeforeUpdate = shortIds.associateWith(::widgetPostItemId)
    val idsAfterUpdate = shortIds.reversed().associateWith(::widgetPostItemId)

    assertThat(idsAfterUpdate).isEqualTo(idsBeforeUpdate)
  }

  @Test
  fun `post IDs are unique for preview posts and distinct from footer`() {
    val postIds = listOf("widget-preview-1", "widget-preview-2", "sxlf4a").map(::widgetPostItemId)

    assertThat(postIds.toSet()).hasSize(postIds.size)
    assertThat(postIds).doesNotContain(WidgetFooterItemId)
    assertThat(postIds).hasSize(3)
    assertThat(postIds.all { it >= 0L && it < WidgetFooterItemId }).isTrue()
  }
}
