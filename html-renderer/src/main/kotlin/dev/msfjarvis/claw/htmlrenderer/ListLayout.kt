/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun RenderList(
  block: ListBlock,
  style: TextStyle,
  color: Color,
  linkStyles: TextLinkStyles,
  quoteBarColor: Color,
  transformDestination: (String) -> String,
  paragraphGap: Int,
  depth: Int,
) {
  val markerRows =
    remember(block) {
      block.children.indices.filter { (block.children[it] as? ListItem)?.markerVisible == true }
    }
  val density = LocalDensity.current
  val minimumGutter = with(density) { 24.sp.roundToPx() }
  val markerGap = with(density) { 8.dp.roundToPx() }
  val policy =
    remember(markerRows, block.children.size, minimumGutter, markerGap) {
      ListMeasurePolicy(markerRows, block.children.size, minimumGutter, markerGap)
    }
  Layout(
    content = {
      block.children.forEach { child ->
        if (child is ListItem && child.markerVisible) {
          val marker =
            when {
              block.ordered -> "${child.ordinal}."
              depth == 1 -> "•"
              depth == 2 -> "◦"
              else -> "▪"
            }
          Text(marker, color = color, style = style, softWrap = false)
        }
        RenderBlocks(
          if (child is ListItem) child.children else persistentListOf(child),
          style,
          color,
          linkStyles,
          quoteBarColor,
          transformDestination,
          paragraphGap,
          listDepth = depth,
        )
      }
    },
    measurePolicy = policy,
  )
}

/** Markers determine a shared gutter before each direct content child is measured once. */
private class ListMeasurePolicy(
  val markerRows: List<Int>,
  rowCount: Int,
  val minimumGutter: Int,
  val markerGap: Int,
) : MeasurePolicy {
  private val markerSlots = markerRows.mapIndexed { index, row -> row + index }
  private val contentSlots =
    (0 until rowCount).map { row ->
      row + markerRows.count { it <= row }
    }

  override fun MeasureScope.measure(
    measurables: List<Measurable>,
    constraints: Constraints,
  ): androidx.compose.ui.layout.MeasureResult {
    val gap = markerGap.coerceAtMost(constraints.maxWidth)
    val markers = markerSlots.map {
      measurables[it].measure(Constraints(maxWidth = constraints.maxWidth - gap))
    }
    val gutter = gutter(markers.maxOfOrNull { it.width } ?: 0, constraints.maxWidth)
    val contents = contentSlots.map {
      measurables[it].measure(Constraints(maxWidth = constraints.maxWidth - gutter))
    }
    val markerByRow = markerRows.withIndex().associate { it.value to markers[it.index] }
    val contentOffsets = IntArray(contents.size)
    val markerOffsets = IntArray(contents.size)
    var height = 0
    var first = AlignmentLine.Unspecified
    var last = AlignmentLine.Unspecified
    contents.forEachIndexed { index, content ->
      val marker = markerByRow[index]
      // Baselines are only needed outside the lookahead pass; a stale lookahead node can still
      // make Compose throw while resolving one during the approach pass.
      val contentFirstBaseline =
        if (isLookingAhead) AlignmentLine.Unspecified
        else content.alignmentLineOrUnspecified(FirstBaseline)
      val markerFirstBaseline =
        if (isLookingAhead || marker == null) AlignmentLine.Unspecified
        else marker.alignmentLineOrUnspecified(FirstBaseline)
      val baselineDelta =
        if (
          contentFirstBaseline != AlignmentLine.Unspecified &&
            markerFirstBaseline != AlignmentLine.Unspecified
        )
          contentFirstBaseline - markerFirstBaseline
        else 0
      // A fallback font's marker line box must not add leading or trailing item space.
      // Content owns row height; the marker's glyph is placed on its descendant baseline.
      contentOffsets[index] = height
      markerOffsets[index] = height + baselineDelta
      if (!isLookingAhead) {
        if (first == AlignmentLine.Unspecified && contentFirstBaseline != AlignmentLine.Unspecified)
          first = height + contentFirstBaseline
        val contentLastBaseline = content.alignmentLineOrUnspecified(LastBaseline)
        if (contentLastBaseline != AlignmentLine.Unspecified) last = height + contentLastBaseline
      }
      height += content.height
    }
    val lines =
      buildMap<AlignmentLine, Int> {
        if (first != AlignmentLine.Unspecified) put(FirstBaseline, first)
        if (last != AlignmentLine.Unspecified) put(LastBaseline, last)
      }
    return layout(
      constraints.constrainWidth(gutter + (contents.maxOfOrNull { it.width } ?: 0)),
      constraints.constrainHeight(height),
      lines,
    ) {
      contents.forEachIndexed { index, content ->
        markerByRow[index]?.let { marker ->
          marker.placeRelative((gutter - gap - marker.width).coerceAtLeast(0), markerOffsets[index])
        }
        content.placeRelative(gutter, contentOffsets[index])
      }
    }
  }

  private fun gutter(markerWidth: Int, width: Int): Int =
    maxOf(minimumGutter.toLong(), markerWidth.toLong() + markerGap)
      .coerceAtMost(width.toLong())
      .toInt()

  override fun IntrinsicMeasureScope.minIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int,
  ): Int = intrinsicWidth(measurables, height, false)

  override fun IntrinsicMeasureScope.maxIntrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int,
  ): Int = intrinsicWidth(measurables, height, true)

  private fun intrinsicWidth(
    measurables: List<IntrinsicMeasurable>,
    height: Int,
    max: Boolean,
  ): Int {
    val gutter =
      gutter(
        markerSlots.maxOfOrNull {
          measurables[it].maxIntrinsicWidth(Constraints.Infinity)
        } ?: 0,
        Constraints.Infinity,
      )
    val contentWidth =
      contentSlots.maxOfOrNull {
        if (max) measurables[it].maxIntrinsicWidth(height)
        else measurables[it].minIntrinsicWidth(height)
      } ?: 0
    return (gutter.toLong() + contentWidth).coerceAtMost(Constraints.Infinity.toLong()).toInt()
  }

  override fun IntrinsicMeasureScope.minIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
  ): Int = intrinsicHeight(measurables, width, false)

  override fun IntrinsicMeasureScope.maxIntrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
  ): Int = intrinsicHeight(measurables, width, true)

  private fun intrinsicHeight(
    measurables: List<IntrinsicMeasurable>,
    width: Int,
    max: Boolean,
  ): Int {
    val gutter =
      gutter(
        markerSlots.maxOfOrNull {
          measurables[it].maxIntrinsicWidth(Constraints.Infinity)
        } ?: 0,
        width,
      )
    return contentSlots
      .sumOf {
        (if (max) measurables[it].maxIntrinsicHeight(width - gutter)
          else measurables[it].minIntrinsicHeight(width - gutter))
          .toLong()
      }
      .coerceAtMost(Constraints.Infinity.toLong())
      .toInt()
  }
}
