/*
 * Copyright © 2021-2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.database.local.SavedPost
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import kotlinx.serialization.SerialName

@KonvertTo(
  value = SavedPost::class,
  mappings =
    [
      Mapping(
        target = "submitterName",
        expression = "it.submitter.username",
      ),
      Mapping(
        target = "submitterAvatarUrl",
        expression = "it.submitter.avatarUrl",
      ),
    ],
)
data class UIPost(
  val shortId: String,
  val createdAt: String,
  val title: String,
  val url: String,
  val description: String,
  val commentCount: Int,
  val commentsUrl: String,
  @SerialName("submitter_user") val submitter: User,
  val tags: List<String>,
  val comments: List<Comment> = emptyList(),
  val isSaved: Boolean = false,
) {
  @KonvertFrom(
    value = SavedPost::class,
    mappings =
      [
        Mapping(
          target = "submitter",
          expression = "User(it.submitterName, \"\", null, it.submitterAvatarUrl, \"\")"
        ),
        Mapping(
          target = "commentCount",
          expression = "it.commentCount ?: 0",
        ),
        Mapping(
          target = "isSaved",
          expression = "true",
        ),
      ],
  )
  companion object
}
