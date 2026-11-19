/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class NavigationTypeTest {
  @Test
  fun `compact width uses bottom navigation`() {
    assertThat(ClawNavigationType.fromSize(WindowWidthSizeClass.Compact))
      .isEqualTo(ClawNavigationType.BOTTOM_NAVIGATION)
  }

  @Test
  fun `medium and expanded widths use a rail`() {
    assertThat(ClawNavigationType.fromSize(WindowWidthSizeClass.Medium))
      .isEqualTo(ClawNavigationType.NAVIGATION_RAIL)
    assertThat(ClawNavigationType.fromSize(WindowWidthSizeClass.Expanded))
      .isEqualTo(ClawNavigationType.NAVIGATION_RAIL)
  }
}
