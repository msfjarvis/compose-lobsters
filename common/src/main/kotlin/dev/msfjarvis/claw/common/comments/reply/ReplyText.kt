/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.fleeksoft.ksoup.Ksoup

internal fun quoteText(commentText: String): String {
  return commentText
    .lines()
    .joinToString(separator = "\n") { line -> "> ${line.trimEnd()}" }
    .plus("\n")
}

internal fun plainTextFromHtml(commentText: String): String {
  return Ksoup.parse(commentText).text().trim()
}

internal fun insertQuote(current: TextFieldValue, commentText: String): TextFieldValue {
  val quoted = quoteText(commentText)
  val range = current.selection
  val newText = buildString {
    append(current.text.substring(0, range.min))
    append(quoted)
    append(current.text.substring(range.max))
  }
  val cursor = range.min + quoted.length
  return TextFieldValue(text = newText, selection = TextRange(cursor))
}
