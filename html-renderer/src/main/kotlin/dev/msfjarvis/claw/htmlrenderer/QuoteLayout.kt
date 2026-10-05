/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf

/** Drawing wraps the inset and internal gaps; the parent owns external sibling gaps. */
@Composable
internal fun QuoteColumn(
  quote: Quote,
  barColor: Color,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  if (quote.children.isEmpty()) return
  val inset = with(LocalDensity.current) { 15.dp.roundToPx() }
  BlockColumn(
    persistentListOf(quote),
    paragraphGap = 0,
    modifier.drawBehind {
      val barWidth = 3.dp.toPx().coerceAtMost(size.width)
      val x = if (layoutDirection == LayoutDirection.Ltr) 0f else size.width - barWidth
      drawRect(barColor, Offset(x, 0f), Size(barWidth, size.height))
    },
    leadingInset = inset,
    content = content,
  )
}
