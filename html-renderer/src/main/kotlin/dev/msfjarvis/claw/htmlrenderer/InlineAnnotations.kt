/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em

internal val IdentityDestination: (String) -> String = { it }

internal fun InlineContent.annotate(
  linkStyles: TextLinkStyles,
  transform: (String) -> String = IdentityDestination,
): AnnotatedString {
  val builder = AnnotatedString.Builder()
  var anchor: Anchor? = null
  var anchorStart = 0
  fun finishAnchor() {
    val current = anchor ?: return
    if (builder.length > anchorStart)
      builder.addLink(
        LinkAnnotation.Url(transform(current.destination), linkStyles),
        anchorStart,
        builder.length,
      )
  }
  runs.forEach { run ->
    if (run.style.anchor != anchor) {
      finishAnchor()
      anchor = run.style.anchor
      anchorStart = builder.length
    }
    val start = builder.length
    builder.append(run.text)
    val style = run.style
    val decorations = buildList {
      if (style.underline) add(TextDecoration.Underline)
      if (style.strike) add(TextDecoration.LineThrough)
    }
    builder.addStyle(
      SpanStyle(
        fontWeight =
          when {
            style.boldDepth > 1 -> FontWeight.Black
            style.boldDepth == 1 -> FontWeight.Bold
            else -> null
          },
        fontStyle = if (style.italic) FontStyle.Italic else null,
        fontFamily = if (style.monospace) FontFamily.Monospace else null,
        fontSize = style.scale.em,
        baselineShift = BaselineShift(style.baseline),
        textDecoration = if (decorations.isEmpty()) null else TextDecoration.combine(decorations),
      ),
      start,
      builder.length,
    )
  }
  finishAnchor()
  return builder.toAnnotatedString()
}
