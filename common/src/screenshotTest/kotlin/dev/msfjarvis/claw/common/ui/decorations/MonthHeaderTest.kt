/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.ui.decorations

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.msfjarvis.claw.common.theme.LobstersTheme

@SuppressLint("ComposePreviewPublic", "ComposeUnstableReceiver")
class MonthHeaderTest {
  @Preview(showBackground = true)
  @Composable
  fun DefaultPreview() {
    LobstersTheme { MonthHeader(label = "April 2023") }
  }
}
