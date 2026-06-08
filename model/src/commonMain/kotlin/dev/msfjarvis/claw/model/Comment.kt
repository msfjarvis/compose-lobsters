/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Comment(
  val shortId: String,
  val comment: String,
  val url: String = "",
  val score: Int = 1,
  val timestamp: Instant,
  val edited: Boolean = false,
  val parentComment: String? = null,
  @SerialName("commenting_user") val user: String = "",
  val isUpvoted: Boolean = false,
)
