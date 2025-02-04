/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

private sealed class Segment {
  data class Text(val content: String) : Segment()

  data class Blockquote(val content: String) : Segment()

  data class Code(val content: String) : Segment()
}

private fun parseSegments(text: String): List<Segment> {
  val segments = mutableListOf<Segment>()
  var i = 0
  val blockquoteStart = "<blockquote>"
  val blockquoteEnd = "</blockquote>"
  val preStart = "<pre>"
  val preEnd = "</pre>"
  val codeStart = "<code"
  val codeEnd = "</code>"

  fun addSegment(content: String, isBlockquote: Boolean = false, isCode: Boolean = false) {
    if (content.isNotBlank()) {
      when {
        isBlockquote -> segments.add(Segment.Blockquote(content))
        isCode -> segments.add(Segment.Code(content))
        else -> segments.add(Segment.Text(content))
      }
    }
  }

  while (i < text.length) {
    val nextBlockquote = text.indexOf(blockquoteStart, i, ignoreCase = true)
    val nextPre = text.indexOf(preStart, i, ignoreCase = true)
    val (nextTag, isBlockquote) =
      when {
        nextBlockquote != -1 && (nextPre == -1 || nextBlockquote < nextPre) ->
          nextBlockquote to true
        nextPre != -1 -> nextPre to false
        else -> -1 to false
      }
    if (nextTag == -1) {
      addSegment(text.substring(i))
      break
    }
    if (nextTag > i) {
      addSegment(text.substring(i, nextTag))
    }
    if (isBlockquote) {
      val endIdx = text.indexOf(blockquoteEnd, nextTag + blockquoteStart.length, ignoreCase = true)
      if (endIdx == -1) {
        addSegment(text.substring(nextTag))
        break
      }
      addSegment(text.substring(nextTag + blockquoteStart.length, endIdx), isBlockquote = true)
      i = endIdx + blockquoteEnd.length
    } else {
      val endIdx = text.indexOf(preEnd, nextTag + preStart.length, ignoreCase = true)
      if (endIdx == -1) {
        addSegment(text.substring(nextTag))
        break
      }
      var codeContent = text.substring(nextTag + preStart.length, endIdx)
      val codeTagStart = codeContent.indexOf(codeStart, ignoreCase = true)
      val codeTagEnd = codeContent.indexOf(codeEnd, ignoreCase = true)
      if (codeTagStart != -1 && codeTagEnd != -1 && codeTagStart < codeTagEnd) {
        val codeOpenEnd = codeContent.indexOf('>', codeTagStart)
        if (codeOpenEnd != -1 && codeOpenEnd < codeTagEnd) {
          codeContent = codeContent.substring(codeOpenEnd + 1, codeTagEnd)
        }
      }
      addSegment(codeContent, isCode = true)
      i = endIdx + preEnd.length
    }
  }

  return segments
}

@Composable
internal fun ThemedRichText(text: String, modifier: Modifier = Modifier) {
  val linkSpanStyle =
    SpanStyle(
      background = MaterialTheme.colorScheme.surfaceVariant,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
      textDecoration = TextDecoration.Underline,
    )

  val segments = parseSegments(text)

  Column(modifier = modifier) {
    for (segment in segments) {
      when (segment) {
        is Segment.Blockquote -> {
          Row(
            modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth().height(IntrinsicSize.Min)
          ) {
            Box(
              modifier =
                Modifier.width(4.dp)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(2.dp))
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text =
                  remember(segment.content) {
                    htmlToAnnotatedString(
                      html = segment.content,
                      compactMode = false,
                      style = HtmlStyle(textLinkStyles = TextLinkStyles(linkSpanStyle)),
                    )
                  },
                style = MaterialTheme.typography.bodyLarge,
                color = contentColorFor(MaterialTheme.colorScheme.background),
              )
            }
          }
        }
        is Segment.Code -> {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
                  shape = RoundedCornerShape(6.dp),
                )
                .padding(12.dp)
          ) {
            Text(
              text = segment.content,
              style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
        is Segment.Text -> {
          Text(
            text =
              remember(segment.content) {
                htmlToAnnotatedString(
                  html = segment.content,
                  compactMode = false,
                  style = HtmlStyle(textLinkStyles = TextLinkStyles(linkSpanStyle)),
                )
              },
            style = MaterialTheme.typography.bodyLarge,
            color = contentColorFor(MaterialTheme.colorScheme.background),
          )
        }
      }
    }
  }
}

@ThemePreviews
@Composable
internal fun ThemedRichTextPreview() {
  val text =
    """
    <h1>Hello <strong>HTML Converter</strong> for Compose</h1>
    <p>This the first paragraph of the sample app running on <strong>Nothing</strong>!</p>
    <ul>
        <li><strong>Bold</strong></li>
        <li><em>Italic</em></li>
        <li><u>Underline</u></li>
        <li><del>Strikethrough</del></li>
        <li><code>Code</code></li>
        <li><a href="https://www.wikipedia.org/">Hyperlink with custom styling</a></li>
        <li><big>Bigger</big> and <small>smaller</small> text</li>
        <li><sup>Super</sup>text and <sub>sub</sub>text</li>
        <li>A nested ordered list:
            <ol>
                <li>Item 1</li>
                <li>Item 2</li>
            </ol>
        </li>
    </ul>
    <dl>
        <dt>Term</dt>
        <dd>Description.</dd>
    </dl>
    A few HTML entities: &raquo; &copy; &laquo; &check;
    <blockquote>A blockquote, indented relatively to the main text.</blockquote>
    <pre><code class="language-toml">Preformatted text, preserving
    line breaks...    and spaces.</code></pre>
    <p><code>Inline code example.</code> You reached the end of the document.<br />Thank you for reading!</p>
    """
      .trimIndent()

  LobstersTheme { ThemedRichText(text = text) }
}
