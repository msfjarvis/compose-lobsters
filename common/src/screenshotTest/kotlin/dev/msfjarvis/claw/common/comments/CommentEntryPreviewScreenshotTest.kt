/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.android.tools.screenshot.PreviewTest
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews

@PreviewTest
@DevicePreviews
@ThemePreviews
@Composable
private fun CommentEntryPreview(
  @PreviewParameter(BooleanPreviewParameterProvider::class) isUpvoted: Boolean
) {
  PreviewCommentEntry(previewCommentNode(isUpvoted))
}

private class BooleanPreviewParameterProvider(
  override val values: Sequence<Boolean> = sequenceOf(true, false)
) : PreviewParameterProvider<Boolean>
