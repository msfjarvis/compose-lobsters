/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class ReplyTextTest {

  @Test
  fun `quoteText prefixes every line with blockquote marker`() {
    assertThat(quoteText("first\nsecond")).isEqualTo("> first\n> second\n")
  }

  @Test
  fun `insertQuote inserts quote at cursor position`() {
    val editor = TextFieldState(initialText = "hello world", initialSelection = TextRange(6))

    insertQuote(editor, "quoted")

    assertThat(editor.text.toString()).isEqualTo("hello > quoted\nworld")
    assertThat(editor.selection.collapsed).isTrue()
    assertThat(editor.selection.start).isEqualTo("hello > quoted\n".length)
  }

  @Test
  fun `insertQuote replaces current selection`() {
    val editor = TextFieldState(initialText = "hello world", initialSelection = TextRange(11, 6))

    insertQuote(editor, "quoted")

    assertThat(editor.text.toString()).isEqualTo("hello > quoted\n")
    assertThat(editor.selection.start).isEqualTo(editor.text.length)
  }

  @Test
  fun `plainTextFromHtml strips markup for quoting`() {
    assertThat(plainTextFromHtml("<p>Hello <strong>world</strong></p><p>Next line</p>"))
      .isEqualTo("Hello world\nNext line")
  }
}
