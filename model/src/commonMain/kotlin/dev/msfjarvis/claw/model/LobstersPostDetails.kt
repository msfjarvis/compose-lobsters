/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LobstersPostDetails(
  val shortId: String,
  val createdAt: String = "",
  val title: String,
  val url: String = "",
  val description: String = "",
  val commentCount: Int = 0,
  val commentsUrl: String = "",
  @SerialName("submitter_user") val submitter: String,
  val tags: List<String> = emptyList(),
  val comments: List<Comment> = emptyList(),
  @SerialName("user_is_author") val userIsAuthor: Boolean = false,
)
