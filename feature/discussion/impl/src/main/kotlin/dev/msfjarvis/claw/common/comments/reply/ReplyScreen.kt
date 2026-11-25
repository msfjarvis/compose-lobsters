/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.ui.ThemedRichText
import dev.msfjarvis.claw.common.ui.preview.DevicePreviews
import dev.msfjarvis.claw.common.ui.preview.ThemePreviews
import dev.msfjarvis.claw.common.ui.rewriteUrlIfLobstersPost
import dev.msfjarvis.claw.feature.discussion.impl.R
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.flow.drop

@Composable
internal fun ReplyScreen(
  postId: String,
  commentId: String,
  commentText: String,
  onReplySubmitted: () -> Unit,
  contentPadding: PaddingValues,
  modifier: Modifier = Modifier,
  viewModel: ReplyViewModel = metroViewModel(key = commentId),
) {
  val uiState = viewModel.uiState
  val editor = rememberSaveable(commentId, saver = TextFieldState.Saver) { TextFieldState() }

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
    editor = editor,
    onEditorChanged = viewModel::clearEditorError,
    onParentCommentClick = viewModel::showQuoteDialog,
    onDismissDialog = viewModel::dismissQuoteDialog,
    onQuote = {
      insertQuote(editor, commentText)
      viewModel.dismissQuoteDialog()
      viewModel.clearEditorError()
    },
    onSubmit = { viewModel.submit(commentId, postId, editor.text.toString()) },
    modifier = modifier,
  )
}

@Composable
internal fun ReplyScreenContent(
  commentText: String,
  uiState: ReplyUiState,
  contentPadding: PaddingValues,
  onEditorChanged: () -> Unit,
  onParentCommentClick: () -> Unit,
  onDismissDialog: () -> Unit,
  onQuote: () -> Unit,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
  scrollState: ScrollState = rememberScrollState(),
  editor: TextFieldState = rememberTextFieldState(),
) {
  val currentOnEditorChanged by rememberUpdatedState(onEditorChanged)
  LaunchedEffect(editor) {
    snapshotFlow { editor.text.toString() to editor.selection }
      .drop(1)
      .collect { currentOnEditorChanged() }
  }
  Box(
    modifier =
      modifier
        .fillMaxSize()
        .padding(contentPadding)
        .consumeWindowInsets(contentPadding)
        .imePadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier =
        Modifier.widthIn(max = 720.dp)
          .fillMaxWidth()
          .fillMaxHeight()
          .verticalScroll(scrollState)
          .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      ElevatedCard(onClick = onParentCommentClick, modifier = Modifier.fillMaxWidth()) {
        ThemedRichText(
          text = commentText,
          modifier = Modifier.padding(16.dp),
          transformDestination = { rewriteUrlIfLobstersPost(it) },
        )
      }
      TextField(
        state = editor,
        label = { Text(stringResource(R.string.reply_editor_label)) },
        supportingText = {
          uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        },
        enabled = !uiState.isSubmitting,
        modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp),
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

internal const val COMMENT_TEXT =
  """
<blockquote>
<p>“Ende” is an anagram of “Deno”</p>
</blockquote>
<p>How is it an <a href="https://en.wikipedia.org/wiki/Anagram" rel="ugc">anagram</a> ?</p>
"""

@ThemePreviews
@DevicePreviews
@Preview
@Composable
private fun ReplyScreenPreview() {
  LobstersTheme {
    ReplyScreenContent(
      commentText = COMMENT_TEXT,
      uiState = ReplyUiState(),
      editor = rememberTextFieldState("Draft"),
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
      commentText = COMMENT_TEXT,
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
      commentText = COMMENT_TEXT,
      uiState = ReplyUiState(isSubmitting = true),
      editor = rememberTextFieldState("Draft"),
      contentPadding = PaddingValues(),
      onEditorChanged = {},
      onParentCommentClick = {},
      onDismissDialog = {},
      onQuote = {},
      onSubmit = {},
    )
  }
}
