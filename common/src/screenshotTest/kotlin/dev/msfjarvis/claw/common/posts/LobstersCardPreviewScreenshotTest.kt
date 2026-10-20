/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.posts

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme

@PreviewTest
@Preview(name = "Comment count above 99", widthDp = 400, showBackground = true)
@Composable
private fun LobstersCardWithOverflowingCommentCountScreenshot() {
  LobstersTheme {
    LobstersCard(
      post = TEST_POST.copy(commentCount = 300),
      isSaved = false,
      isRead = false,
      postActions = TEST_POST_ACTIONS,
    )
  }
}
