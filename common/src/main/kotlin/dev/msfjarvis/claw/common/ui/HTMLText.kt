/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.text.style.QuoteSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import dev.msfjarvis.claw.common.theme.LobstersTheme

private const val URL_TAG = "url_tag"

@Composable
fun HTMLText(
  text: String,
  modifier: Modifier = Modifier,
  style: TextStyle = MaterialTheme.typography.bodyLarge,
  softWrap: Boolean = true,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  fontSize: TextUnit = 14.sp,
  flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT,
  customSpannedHandler: ((Spanned) -> AnnotatedString)? = null
) {
  val content = text.asHTML(fontSize, flags, customSpannedHandler)
  val uriHandler = LocalUriHandler.current
  ClickableText(
    modifier = modifier,
    text = content,
    style = style,
    softWrap = softWrap,
    overflow = overflow,
    maxLines = maxLines,
    onTextLayout = onTextLayout,
    onClick = { offset ->
      content.getStringAnnotations(URL_TAG, offset, offset).firstOrNull()?.let { stringAnnotation ->
        uriHandler.openUri(stringAnnotation.item)
      }
    }
  )
}

@Composable
private fun String.asHTML(
  fontSize: TextUnit,
  flags: Int,
  customSpannedHandler: ((Spanned) -> AnnotatedString)? = null
) = buildAnnotatedString {
  val spanned = HtmlCompat.fromHtml(this@asHTML, flags)
  val spans = spanned.getSpans(0, spanned.length, Any::class.java)

  if (customSpannedHandler != null) {
    append(customSpannedHandler(spanned))
  } else {
    append(spanned.toString())
  }

  spans
    .filter { it !is BulletSpan }
    .forEach { span ->
      val start = spanned.getSpanStart(span)
      val end = spanned.getSpanEnd(span)
      when (span) {
        is RelativeSizeSpan -> span.spanStyle(fontSize)
        is StyleSpan -> span.spanStyle()
        is UnderlineSpan -> span.spanStyle()
        is ForegroundColorSpan -> span.spanStyle()
        is StrikethroughSpan -> span.spanStyle()
        is SuperscriptSpan -> span.spanStyle()
        is SubscriptSpan -> span.spanStyle()
        is QuoteSpan -> span.spanStyle()
        is URLSpan -> {
          addStringAnnotation(tag = URL_TAG, annotation = span.url, start = start, end = end)
          span.spanStyle()
        }
        else -> {
          null
        }
      }?.let { spanStyle -> addStyle(spanStyle, start, end) }
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun URLSpan.spanStyle(): SpanStyle =
  SpanStyle(
    background = MaterialTheme.colorScheme.surfaceVariant,
    color = MaterialTheme.colorScheme.onSurface,
    fontWeight = FontWeight.Bold,
    textDecoration = TextDecoration.Underline,
  )

@Suppress("UnusedReceiverParameter")
private fun UnderlineSpan.spanStyle(): SpanStyle =
  SpanStyle(textDecoration = TextDecoration.Underline)

private fun ForegroundColorSpan.spanStyle(): SpanStyle = SpanStyle(color = Color(foregroundColor))

@Suppress("UnusedReceiverParameter")
private fun StrikethroughSpan.spanStyle(): SpanStyle =
  SpanStyle(textDecoration = TextDecoration.LineThrough)

private fun RelativeSizeSpan.spanStyle(fontSize: TextUnit): SpanStyle =
  SpanStyle(fontSize = (fontSize.value * sizeChange).sp)

@Suppress("UnusedReceiverParameter")
private fun QuoteSpan.spanStyle(): SpanStyle = SpanStyle(fontStyle = FontStyle.Italic)

private fun StyleSpan.spanStyle(): SpanStyle? =
  when (style) {
    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
    Typeface.BOLD_ITALIC ->
      SpanStyle(
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
      )
    else -> null
  }

@Suppress("UnusedReceiverParameter")
private fun SubscriptSpan.spanStyle(): SpanStyle =
  SpanStyle(baselineShift = BaselineShift.Subscript)

@Suppress("UnusedReceiverParameter")
private fun SuperscriptSpan.spanStyle(): SpanStyle =
  SpanStyle(baselineShift = BaselineShift.Superscript)

@Composable
private fun ClickableText(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  style: TextStyle = TextStyle.Default,
  softWrap: Boolean = true,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  onClick: (Int) -> Unit
) {
  val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
  val pressIndicator =
    Modifier.pointerInput(onClick) {
      detectTapGestures { pos ->
        layoutResult.value?.let { layoutResult -> onClick(layoutResult.getOffsetForPosition(pos)) }
      }
    }
  Text(
    text = text,
    modifier = modifier.then(pressIndicator),
    style = style,
    softWrap = softWrap,
    overflow = overflow,
    maxLines = maxLines,
    onTextLayout = {
      layoutResult.value = it
      onTextLayout(it)
    }
  )
}

@Preview
@Composable
fun HTMLTextPreview() {
  LobstersTheme {
    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
      HTMLText(
        text =
          """
      <h3 id="heading">Heading</h3>
      <p>This is a paragraph body</p>
      <pre><code>This is <span class="hljs-selector-tag">a</span> <span class="hljs-selector-tag">code</span> block
      </code></pre><p>This is an <code>inline code block</code></p>
      <blockquote><p>This is a blockquote</p></blockquote>
      <p><a href="https://github.com/msfjarvis/compose-lobsters">This is a link</a></p>
      <p><img src="https://avatars.githubusercontent.com/u/13348378?v=4" alt="Image"></p>
          """
            .trimIndent()
      )
    }
  }
}
