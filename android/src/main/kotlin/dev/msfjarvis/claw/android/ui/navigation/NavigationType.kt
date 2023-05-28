/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class ClawNavigationType {
  BOTTOM_NAVIGATION,
  NAVIGATION_RAIL;

  companion object {
    fun fromSize(windowSize: WindowWidthSizeClass): ClawNavigationType {
      return when (windowSize) {
        WindowWidthSizeClass.Compact -> BOTTOM_NAVIGATION
        WindowWidthSizeClass.Medium,
        WindowWidthSizeClass.Expanded -> NAVIGATION_RAIL
        else -> BOTTOM_NAVIGATION
      }
    }
  }
}
