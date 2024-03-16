/*
 * Copyright © 2021-2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.drewhamilton.poko.Poko
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Poko
@KonvertTo(
  value = UIPost::class,
  mappings = [Mapping(target = "submitterName", expression = "it.submitter.username")],
)
class LobstersPost(
  val shortId: String,
  val createdAt: String,
  val title: String,
  val url: String,
  val description: String,
  val commentCount: Int,
  val commentsUrl: String,
  @SerialName("submitter_user") val submitter: String,
  val tags: List<String>,
)
