/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
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

@PreviewTest
@ThemePreviews
@Composable
private fun ReplyScreenLongCommentPreviewScreenshot() {
  val scrollState = rememberScrollState(initial = 10_000)
  LobstersTheme {
    ReplyScreenContent(
      commentText =
        """
        This is a very long parent comment intended to exercise the reply screen when the quoted target
        takes up a lot of vertical space. It should be possible to scroll this content out of view and
        still reach the reply editor and submit button without them disappearing behind the keyboard.

        Paragraph two adds more height so that the preview meaningfully captures the long-content state.
        We want this screenshot to guard against regressions where the editor becomes unreachable.

        Paragraph three is just more realistic body text, similar to a real Lobsters thread where someone
        writes a multi-paragraph explanation with examples, caveats, and extra context.

        Paragraph four keeps the card tall enough to validate the scrolling layout on phone-sized screens.
        """
          .trimIndent(),
      uiState = ReplyUiState(),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
      scrollState = scrollState,
    )
  }
}
