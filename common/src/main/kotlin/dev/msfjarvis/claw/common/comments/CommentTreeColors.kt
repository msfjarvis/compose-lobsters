/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.ui.graphics.Color

object CommentTreeColors {

  // Fully opaque ARGB colors, ordered so neighboring depth guides have strongly different hues.
  // Every entry has at least 3:1 contrast against both app backgrounds.
  private val palette =
    listOf(
      Color(0xFFDD3C9C),
      Color(0xFF04A904),
      Color(0xFF8C1AFF),
      Color(0xFF2A760A),
      Color(0xFFE70AFF),
      Color(0xFF009E6F),
      Color(0xFFFF0ACE),
      Color(0xFF6C6C13),
      Color(0xFF0A54FF),
      Color(0xFFFA3200),
      Color(0xFF1F8CF9),
      Color(0xFFDF750C),
      Color(0xFFB04EDA),
      Color(0xFF9C5E21),
      Color(0xFF136DAA),
      Color(0xFFFF1A47),
      Color(0xFF1B9FC0),
      Color(0xFFC03C1B),
      Color(0xFF19766D),
      Color(0xFFDA4E6A),
    )

  fun colorForDepth(depth: Int): Color = palette[depth.mod(palette.size)]
}
