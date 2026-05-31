/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ReplyTextTest {

  @Test
  fun `quoteText prefixes every line with blockquote marker`() {
    assertThat(quoteText("first\nsecond")).isEqualTo("> first\n> second\n")
  }

  @Test
  fun `insertQuote inserts quote at cursor position`() {
    val initial = TextFieldValue(text = "hello world", selection = TextRange(6))

    val result = insertQuote(initial, "quoted")

    assertThat(result.text).isEqualTo("hello > quoted\nworld")
    assertThat(result.selection.collapsed).isTrue()
    assertThat(result.selection.start).isEqualTo("hello > quoted\n".length)
  }

  @Test
  fun `insertQuote replaces current selection`() {
    val initial = TextFieldValue(text = "hello world", selection = TextRange(6, 11))

    val result = insertQuote(initial, "quoted")

    assertThat(result.text).isEqualTo("hello > quoted\n")
    assertThat(result.selection.start).isEqualTo(result.text.length)
  }

  @Test
  fun `plainTextFromHtml strips markup for quoting`() {
    assertThat(plainTextFromHtml("<p>Hello <strong>world</strong></p><p>Next line</p>"))
      .isEqualTo("Hello world Next line")
  }
}
