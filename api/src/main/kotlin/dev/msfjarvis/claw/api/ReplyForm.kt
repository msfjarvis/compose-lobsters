/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api

data class ReplyForm(
  val authenticityToken: String,
  val storyId: String,
  val method: String,
  val parentCommentShortId: String,
)
