/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slack.eithernet.ApiResult
import dev.msfjarvis.claw.api.AuthenticatedLobstersApi
import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Inject
@ViewModelKey
@ContributesIntoMap(AppScope::class)
class ReplyViewModel(
  private val api: AuthenticatedLobstersApi,
  @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
  internal var uiState by mutableStateOf(ReplyUiState())
    private set

  fun updateEditor(value: TextFieldValue) {
    uiState = uiState.copy(editor = value, errorMessage = null)
  }

  fun showQuoteDialog() {
    uiState = uiState.copy(isQuoteDialogOpen = true)
  }

  fun dismissQuoteDialog() {
    uiState = uiState.copy(isQuoteDialogOpen = false)
  }

  fun insertQuote(commentText: String) {
    uiState =
      uiState.copy(
        editor = insertQuote(uiState.editor, commentText),
        isQuoteDialogOpen = false,
        errorMessage = null,
      )
  }

  fun clearSubmitSucceeded() {
    uiState = uiState.copy(submitSucceeded = false)
  }

  fun submit(commentId: String, postId: String) {
    val replyText = uiState.editor.text.trim()
    if (replyText.isBlank()) {
      uiState = uiState.copy(errorMessage = "Reply cannot be blank")
      return
    }
    uiState = uiState.copy(isSubmitting = true, errorMessage = null)
    viewModelScope.launch {
      val result = withContext(ioDispatcher) { api.reply(commentId, postId, replyText) }
      uiState =
        when (result) {
          is ApiResult.Success -> uiState.copy(isSubmitting = false, submitSucceeded = true)
          is ApiResult.Failure.NetworkFailure ->
            uiState.copy(
              isSubmitting = false,
              errorMessage = result.error.message ?: "Failed to post reply",
            )
          is ApiResult.Failure.HttpFailure ->
            uiState.copy(isSubmitting = false, errorMessage = "Failed to post reply")
          is ApiResult.Failure.ApiFailure ->
            uiState.copy(isSubmitting = false, errorMessage = "Failed to post reply")
          is ApiResult.Failure.UnknownFailure ->
            uiState.copy(
              isSubmitting = false,
              errorMessage = result.error.message ?: "Failed to post reply",
            )
        }
    }
  }
}
