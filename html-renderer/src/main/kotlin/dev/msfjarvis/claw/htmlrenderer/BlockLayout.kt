/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlinx.collections.immutable.ImmutableList

/** Gap requests combine with max (Boolean OR for the two supported gap sizes), never sum. */
internal fun blockGaps(blocks: List<Block>, paragraphGap: Int): List<Int> =
  blocks.zipWithNext().mapIndexed { index, (previous, next) ->
    when {
      previous is HrSeparator || next is HrSeparator ->
        if (
          previous is HrSeparator &&
            next !is HrSeparator &&
            blocks.take(index).any { it !is HrSeparator }
        )
          paragraphGap
        else 0
      previous.after || next.before -> paragraphGap
      else -> 0
    }
  }

@Composable
internal fun BlockColumn(
  blocks: ImmutableList<Block>,
  paragraphGap: Int,
  modifier: Modifier = Modifier,
  leadingInset: Int = 0,
  content: @Composable () -> Unit,
) {
  val policy =
    remember(blocks, paragraphGap, leadingInset) {
      BlockMeasurePolicy(blockGaps(blocks, paragraphGap), leadingInset)
    }
  Layout(content = content, modifier = modifier, measurePolicy = policy)
}

private class BlockMeasurePolicy(val gaps: List<Int>, val leadingInset: Int) : MeasurePolicy {
  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints,
  ): androidx.compose.ui.layout.MeasureResult {
    val inset = leadingInset.coerceIn(0, constraints.maxWidth)
    val children = measurables.map {
      it.measure(Constraints(maxWidth = (constraints.maxWidth - inset).coerceAtLeast(0)))
    }
    val offsets = mutableListOf<Int>()
    var height = 0
    var first = AlignmentLine.Unspecified
    var last = AlignmentLine.Unspecified
    children.forEachIndexed { index, child ->
      if (index > 0) height += gaps[index - 1]
      offsets += height
      if (first == AlignmentLine.Unspecified && child[FirstBaseline] != AlignmentLine.Unspecified)
        first = height + child[FirstBaseline]
      if (child[LastBaseline] != AlignmentLine.Unspecified) last = height + child[LastBaseline]
      height += child.height
    }
    val lines =
      buildMap<AlignmentLine, Int> {
        if (first != AlignmentLine.Unspecified) put(FirstBaseline, first)
        if (last != AlignmentLine.Unspecified) put(LastBaseline, last)
      }
    return layout(
      constraints.constrainWidth((children.maxOfOrNull { it.width } ?: 0) + inset),
      constraints.constrainHeight(height),
      lines,
    ) {
      children.forEachIndexed { index, child -> child.placeRelative(inset, offsets[index]) }
    }
  }

  override fun IntrinsicMeasureScope.minIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int,
  ): Int = addInset(measurables.maxOfOrNull { it.minIntrinsicWidth(height) } ?: 0)

  override fun IntrinsicMeasureScope.maxIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int,
  ): Int = addInset(measurables.maxOfOrNull { it.maxIntrinsicWidth(height) } ?: 0)

  override fun IntrinsicMeasureScope.minIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
  ): Int = intrinsicHeight(measurables, width, false)

  override fun IntrinsicMeasureScope.maxIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
  ): Int = intrinsicHeight(measurables, width, true)

  private fun addInset(width: Int): Int =
    (width.toLong() + leadingInset).coerceAtMost(Constraints.Infinity.toLong()).toInt()

  private fun intrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
    max: Boolean,
  ): Int {
    val contentWidth = (width - leadingInset.coerceAtMost(width)).coerceAtLeast(0)
    return (measurables.sumOf {
        (if (max) it.maxIntrinsicHeight(contentWidth) else it.minIntrinsicHeight(contentWidth))
          .toLong()
      } + gaps.sumOf { it.toLong() })
      .coerceAtMost(Constraints.Infinity.toLong())
      .toInt()
  }
}
