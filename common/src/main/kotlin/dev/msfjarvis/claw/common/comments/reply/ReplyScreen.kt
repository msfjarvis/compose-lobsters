/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.R
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun ReplyScreen(
  commentId: String,
  contentPadding: PaddingValues,
  postId: String,
  onReplySubmitted: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: ReplyViewModel = metroViewModel(key = commentId),
) {
  val uiState = viewModel.uiState
  val commentText = viewModel.getCommentText(commentId)

  LaunchedEffect(uiState.submitSucceeded) {
    if (uiState.submitSucceeded) {
      onReplySubmitted()
      viewModel.clearSubmitSucceeded()
    }
  }

  ReplyScreenContent(
    commentText = commentText,
    uiState = uiState,
    contentPadding = contentPadding,
    onEditorChanged = viewModel::updateEditor,
    onParentCommentClick = viewModel::showQuoteDialog,
    onDismissDialog = viewModel::dismissQuoteDialog,
    onQuote = { viewModel.insertQuote(commentText) },
    onSubmit = { viewModel.submit(commentId, postId) },
    modifier = modifier,
  )
}

@Composable
internal fun ReplyScreenContent(
  commentText: String,
  uiState: ReplyUiState,
  contentPadding: PaddingValues,
  onEditorChanged: (TextFieldValue) -> Unit,
  onParentCommentClick: () -> Unit,
  onDismissDialog: () -> Unit,
  onQuote: () -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier =
      modifier
        .fillMaxSize()
        .padding(contentPadding)
        .consumeWindowInsets(contentPadding)
        .imePadding()
        .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    ElevatedCard(onClick = onParentCommentClick, modifier = Modifier.fillMaxWidth()) {
      Text(text = commentText, modifier = Modifier.padding(16.dp))
    }
    TextField(
      value = uiState.editor,
      onValueChange = onEditorChanged,
      label = { Text(stringResource(R.string.reply_editor_label)) },
      supportingText = {
        uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
      },
      enabled = !uiState.isSubmitting,
      modifier = Modifier.fillMaxWidth().weight(1f),
    )
    Button(
      onClick = onSubmit,
      enabled = !uiState.isSubmitting,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
      Text(text = stringResource(R.string.submit_reply))
    }
  }

  if (uiState.isQuoteDialogOpen) {
    AlertDialog(
      onDismissRequest = onDismissDialog,
      title = { Text(stringResource(R.string.in_reply_to)) },
      text = { Text(commentText) },
      dismissButton = {
        TextButton(onClick = onDismissDialog) { Text(stringResource(R.string.cancel)) }
      },
      confirmButton = { TextButton(onClick = onQuote) { Text(stringResource(R.string.quote)) } },
    )
  }
}

@ThemePreviews
@DevicePreviews
@Preview
@Composable
private fun ReplyScreenPreview() {
  LobstersTheme {
    ReplyScreenContent(
      commentText = "Parent comment body",
      uiState = ReplyUiState(editor = TextFieldValue("Draft")),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
    )
  }
}

@ThemePreviews
@Composable
private fun ReplyScreenDialogPreview() {
  LobstersTheme {
    ReplyScreenContent(
      commentText = "Parent comment body",
      uiState = ReplyUiState(isQuoteDialogOpen = true),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
    )
  }
}

@ThemePreviews
@Composable
private fun ReplyScreenSubmittingPreview() {
  LobstersTheme {
    ReplyScreenContent(
      commentText = "Parent comment body",
      uiState = ReplyUiState(editor = TextFieldValue("Draft"), isSubmitting = true),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
    )
  }
}
