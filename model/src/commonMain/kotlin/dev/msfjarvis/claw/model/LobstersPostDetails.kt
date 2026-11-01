/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlinx.serialization.Serializable

@Serializable
class LobstersPostDetails(
  val stories: List<LobstersPost>,
  val comments: List<Comment>,
  val commentsUrl: String,
  val commentCount: Int,
)
