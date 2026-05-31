/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.comments.reply

import androidx.compose.ui.text.input.TextFieldValue

internal data class ReplyUiState(
  val editor: TextFieldValue = TextFieldValue(),
  val isQuoteDialogOpen: Boolean = false,
  val isSubmitting: Boolean = false,
  val errorMessage: String? = null,
  val submitSucceeded: Boolean = false,
)
