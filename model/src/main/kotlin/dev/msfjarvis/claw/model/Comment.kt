/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.serialization.JavaInstantSerializer
import java.time.temporal.TemporalAccessor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Comment(
  val shortId: String,
  val comment: String,
  val url: String,
  val score: Int,
  @Serializable(with = JavaInstantSerializer::class) val createdAt: TemporalAccessor,
  @Serializable(with = JavaInstantSerializer::class) val updatedAt: TemporalAccessor,
  val indentLevel: Int,
  @SerialName("commenting_user") val user: User,
)
