/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that represents light and dark themes. Add this annotation to a
 * composable to render the both themes.
 */
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  name = "Light theme",
  showBackground = true,
  backgroundColor = 0xFFFFFF,
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  name = "Dark theme",
  showBackground = true,
  backgroundColor = 0x000000,
)
annotation class ThemePreviews

/**
 * Multipreview annotation that represents various device sizes. Add this annotation to a composable
 * to render various devices.
 */
@Preview(name = "phone", device = "spec:width=360dp,height=640dp,dpi=480")
@Preview(name = "landscape", device = "spec:width=640dp,height=360dp,dpi=480")
@Preview(name = "foldable", device = "spec:width=673dp,height=841dp,dpi=480")
@Preview(name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=480")
annotation class DevicePreviews
