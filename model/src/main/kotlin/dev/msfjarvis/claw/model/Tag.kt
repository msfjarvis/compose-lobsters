/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.drewhamilton.poko.Poko
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Poko
class Tag(
  val tag: String,
  val description: String,
  val privileged: Boolean,
  val active: Boolean,
  val category: String,
  @SerialName("is_media") val isMedia: Boolean,
  @SerialName("hotness_mod") val hotnessMod: Double,
)
