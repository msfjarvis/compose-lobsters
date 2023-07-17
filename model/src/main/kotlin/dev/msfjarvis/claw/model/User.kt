/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Poko
class User(
  val username: String,
  val about: String,
  @SerialName("invited_by_user") val invitedBy: String?,
  val avatarUrl: String,
  val createdAt: String,
)
