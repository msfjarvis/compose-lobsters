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
class LobstersPost(
  val shortId: String,
  val createdAt: String = "",
  val title: String,
  val url: String = "",
  val description: String = "",
  val commentCount: Int = 0,
  val commentsUrl: String = "",
  @SerialName("submitter_user") val submitter: String,
  @SerialName("user_is_author") val userIsAuthor: Boolean = false,
  val tags: List<String> = emptyList(),
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as LobstersPost

    if (commentCount != other.commentCount) return false
    if (userIsAuthor != other.userIsAuthor) return false
    if (shortId != other.shortId) return false
    if (createdAt != other.createdAt) return false
    if (title != other.title) return false
    if (url != other.url) return false
    if (description != other.description) return false
    if (commentsUrl != other.commentsUrl) return false
    if (submitter != other.submitter) return false
    if (tags != other.tags) return false

    return true
  }

  override fun hashCode(): Int {
    var result = commentCount
    result = 31 * result + userIsAuthor.hashCode()
    result = 31 * result + shortId.hashCode()
    result = 31 * result + createdAt.hashCode()
    result = 31 * result + title.hashCode()
    result = 31 * result + url.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + commentsUrl.hashCode()
    result = 31 * result + submitter.hashCode()
    result = 31 * result + tags.hashCode()
    return result
  }
}
