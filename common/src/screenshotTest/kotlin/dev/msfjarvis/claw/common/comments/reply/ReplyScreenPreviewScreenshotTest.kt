/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@PreviewTest
@DevicePreviews
@ThemePreviews
@Composable
private fun ReplyScreenPreviewScreenshot() {
  LobstersTheme {
    ReplyScreenContent(
      commentText = "Parent comment body",
      uiState = ReplyUiState(),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
    )
  }
}
