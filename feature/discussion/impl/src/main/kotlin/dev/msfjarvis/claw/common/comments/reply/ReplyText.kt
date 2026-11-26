/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import com.fleeksoft.ksoup.select.NodeTraversor
import com.fleeksoft.ksoup.select.NodeVisitor

internal fun quoteText(commentText: String): String {
  return commentText
    .lines()
    .joinToString(separator = "\n") { line -> "> ${line.trimEnd()}" }
    .plus("\n")
}

private val BLOCK_TAGS =
  setOf("p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "li", "blockquote", "pre")

internal fun plainTextFromHtml(commentText: String): String {
  val sb = StringBuilder()
  NodeTraversor.traverse(
    object : NodeVisitor {
      override fun head(node: Node, depth: Int) {
        when {
          node is TextNode -> sb.append(node.text())
          node is Element && node.tagName() == "br" -> sb.append("\n")
          node is Element &&
            node.tagName() in BLOCK_TAGS &&
            sb.isNotEmpty() &&
            !sb.endsWith("\n") -> sb.append("\n")
        }
      }

      override fun tail(node: Node, depth: Int) {
        if (node is Element && node.tagName() in BLOCK_TAGS && !sb.endsWith("\n")) {
          sb.append("\n")
        }
      }
    },
    Ksoup.parse(commentText).body(),
  )
  return sb.toString().trim()
}

internal fun insertQuote(editor: TextFieldState, commentText: String) {
  val quoted = quoteText(commentText)
  editor.edit {
    val start = selection.min
    replace(start, selection.max, quoted)
    selection = TextRange(start + quoted.length)
  }
}
