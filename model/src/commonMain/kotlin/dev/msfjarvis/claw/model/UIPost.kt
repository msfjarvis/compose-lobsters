/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName

@Suppress("LongParameterList")
data class UIPost(
  val shortId: String,
  val createdAt: String,
  val title: String,
  val url: String,
  val description: String,
  val commentCount: Int,
  val commentsUrl: String,
  @SerialName("submitter_user") val submitter: String,
  val tags: List<String>,
  val comments: List<Comment> = emptyList(),
  @SerialName("user_is_author") val userIsAuthor: Boolean = false,
) {
  companion object
}
