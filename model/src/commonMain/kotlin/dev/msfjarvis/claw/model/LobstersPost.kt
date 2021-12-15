package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LobstersPost(
  @SerialName("short_id") val shortId: String,
  @SerialName("created_at") val createdAt: String,
  val title: String,
  val url: String,
  val score: Int,
  val flags: Int,
  @SerialName("comment_count") val commentCount: Int,
  val description: String,
  @SerialName("comments_url") val commentsUrl: String,
  @SerialName("submitter_user") val submitter: User,
  val tags: List<String>,
)
