package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LobstersPost(
  @SerialName("short_id") val shortId: String,
  @SerialName("short_id_url") val shortIdUrl: String,
  @SerialName("created_at") val createdAt: String,
  val title: String,
  val url: String,
  val score: Long,
  val flags: Long,
  @SerialName("comment_count") val commentCount: Long,
  val description: String,
  @SerialName("comments_url") val commentsUrl: String,
  @SerialName("submitter_user") val submitter: User,
  val tags: List<String>,
)
