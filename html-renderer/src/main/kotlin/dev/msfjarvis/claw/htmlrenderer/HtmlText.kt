/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList

/** Renders Claw's supported HTML subset. Styling and URL policy belong to the caller. */
@Composable
public fun HtmlText(
  html: String,
  style: TextStyle,
  color: Color,
  linkStyles: TextLinkStyles,
  quoteBarColor: Color,
  modifier: Modifier = Modifier,
  transformDestination: (String) -> String = IdentityDestination,
) {
  val document = remember(html) { parseHtml(html) }
  val density = LocalDensity.current
  val gap =
    with(density) {
      val fontSize =
        if (style.fontSize.isSpecified && style.fontSize.isSp) style.fontSize else 16.sp
      when {
        style.lineHeight.isSp -> style.lineHeight.roundToPx()
        style.lineHeight.isEm -> (fontSize * style.lineHeight.value).roundToPx()
        else -> (fontSize * 1.5f).roundToPx()
      }.coerceAtLeast(0)
    }
  RenderBlocks(
    document.blocks,
    style,
    color,
    linkStyles,
    quoteBarColor,
    transformDestination,
    gap,
    modifier,
  )
}

/** Link targets must not expand into ordinary text in adjacent tight list rows. */
@Composable
private fun InlineText(
  text: AnnotatedString,
  color: Color,
  style: TextStyle,
  modifier: Modifier = Modifier,
) {
  val configuration = LocalViewConfiguration.current
  val linkConfiguration =
    remember(configuration) {
      object : ViewConfiguration by configuration {
        override val minimumTouchTargetSize: DpSize = DpSize.Zero
      }
    }
  // Scope this to inline Text, preserving the caller's comment-row gesture configuration.
  // Native link semantics, focus, hit-path clipping and URI activation remain owned by Text.
  CompositionLocalProvider(LocalViewConfiguration provides linkConfiguration) {
    Text(text, modifier, color = color, style = style)
  }
}

@Composable
internal fun RenderBlocks(
  blocks: ImmutableList<Block>,
  style: TextStyle,
  color: Color,
  linkStyles: TextLinkStyles,
  quoteBarColor: Color,
  transformDestination: (String) -> String,
  paragraphGap: Int,
  modifier: Modifier = Modifier,
  listDepth: Int = 0,
) {
  BlockColumn(blocks, paragraphGap, modifier, suppressListBaselines = listDepth == 0) {
    blocks.forEach { block ->
      when (block) {
        is TextBlock -> {
          val text =
            remember(block.content, linkStyles, transformDestination) {
              block.content.annotate(linkStyles, transformDestination)
            }
          val headingModifier =
            if (block.kind == TextKind.Heading) Modifier.semantics { heading() } else Modifier
          InlineText(text, color, style, headingModifier)
        }
        // Quotes preserve, not increment, list depth.
        is Quote ->
          QuoteColumn(block, quoteBarColor) {
            RenderBlocks(
              block.children,
              style,
              color,
              linkStyles,
              quoteBarColor,
              transformDestination,
              paragraphGap,
              listDepth = listDepth,
            )
          }
        is ListBlock ->
          RenderList(
            block,
            style,
            color,
            linkStyles,
            quoteBarColor,
            transformDestination,
            paragraphGap,
            listDepth + 1,
          )
        is ListItem ->
          RenderBlocks(
            block.children,
            style,
            color,
            linkStyles,
            quoteBarColor,
            transformDestination,
            paragraphGap,
            listDepth = listDepth,
          )
        is DefinitionDescription -> {
          val inset = with(LocalDensity.current) { 24.sp.roundToPx() }
          BlockColumn(block.children, paragraphGap = 0, leadingInset = inset) {
            RenderBlocks(
              block.children,
              style,
              color,
              linkStyles,
              quoteBarColor,
              transformDestination,
              paragraphGap,
              listDepth = listDepth,
            )
          }
        }
        HrSeparator -> Spacer(Modifier)
      }
    }
  }
}
