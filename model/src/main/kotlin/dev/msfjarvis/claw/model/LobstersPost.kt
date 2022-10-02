@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LobstersPost(
  @SerialName("short_id") val shortId: String,
  @SerialName("created_at") val createdAt: String,
  val title: String,
  val url: String,
  val description: String,
  @SerialName("comment_count") val commentCount: Int,
  @SerialName("comments_url") val commentsUrl: String,
  @SerialName("submitter_user") val submitter: User,
  val tags: List<String>,
)
