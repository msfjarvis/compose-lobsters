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
class Tag(
  val tag: String,
  val description: String,
  val privileged: Boolean = false,
  val active: Boolean = true,
  val category: String = "",
  @SerialName("is_media") val isMedia: Boolean = false,
  @SerialName("hotness_mod") val hotnessMod: Double = 0.0,
)
