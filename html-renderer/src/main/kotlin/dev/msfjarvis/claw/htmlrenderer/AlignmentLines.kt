/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Placeable

/**
 * Compose can throw an NPE while resolving an alignment line through a detached lookahead node.
 * Treat that line as unavailable; alignment is optional and the measured layout remains valid.
 */
internal fun Placeable.alignmentLineOrUnspecified(line: AlignmentLine): Int =
  try {
    this[line]
  } catch (_: NullPointerException) {
    AlignmentLine.Unspecified
  }
