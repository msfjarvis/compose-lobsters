/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.ui.graphics.Color

enum class ThemeMode {
  LIGHT,
  DARK,
}

object CommentTreeColors {

  /*
   * Light theme:
   * Lower luminance, lower chroma.
   * Prevents neon appearance on white backgrounds.
   */
  private val lightPalette =
    listOf(
      Color(0x3D5A80FF), // muted blue
      Color(0x6D597AFF), // muted violet
      Color(0xB56576FF), // dusty rose
      Color(0x9C6644FF), // brown amber
      Color(0x4D7C59FF), // muted green
      Color(0x4D6A6DFF), // desaturated cyan
      Color(0x6B705CFF), // olive
      Color(0x9A6B4FFF), // muted orange
    )

  /*
   * Dark theme:
   * Higher luminance required for visibility,
   * but still slightly desaturated.
   */
  private val darkPalette =
    listOf(
      Color(0x7EAEEFFF), // soft blue
      Color(0xB8A1E3FF), // lavender
      Color(0xE598A8FF), // soft rose
      Color(0xD9A066FF), // amber
      Color(0x7BC99FFF), // emerald
      Color(0x7FC8D6FF), // cyan
      Color(0xB7C77AFF), // lime
      Color(0xE0A37AFF), // orange
    )

  fun colorForDepth(
    depth: Int,
    theme: ThemeMode,
  ): Color {
    val palette =
      when (theme) {
        ThemeMode.LIGHT -> lightPalette
        ThemeMode.DARK -> darkPalette
      }

    return palette[depth.mod(palette.size)]
  }
}
