package dev.msfjarvis.lobsters.ui.comments

import androidx.compose.ui.graphics.Color

object CommentTreeColor {
  private val colors =
    arrayOf(
      Color(0xFFFFC40D),
      Color(0xFF2D89EF),
      Color(0xFFB91D47),
      Color(0xFF00ABA9),
      Color(0xFFE3A21A),
      Color(0xFF99B433),
      Color(0xFF7E3878),
      Color(0xFFFFB300),
      Color(0xFFFFFFFF),
      Color(0xFF00A300),
      Color(0xFF2B5797),
      Color(0xFF9F00A7),
      Color(0xFF603CBA),
      Color(0xFFEE1111),
      Color(0xFFEFF4FF),
      Color(0xFFDA532C),
      Color(0xFFFF0097),
      Color(0xFF1E7145),
    )

  val size = colors.size

  operator fun get(idx: Int): Color = colors[idx]
}
