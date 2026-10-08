/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.htmlrenderer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(name = "Light", widthDp = 320)
@Preview(name = "Narrow large font", widthDp = 160, heightDp = 2200, fontScale = 2f)
@Composable
private fun HtmlFormattingLight() {
  HtmlTextPreviewContent()
}

@PreviewTest
@Preview(name = "Dark RTL", widthDp = 320, heightDp = 1600, fontScale = 2f)
@Composable
private fun HtmlFormattingDarkRtl() {
  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    HtmlTextPreviewContent(dark = true)
  }
}
