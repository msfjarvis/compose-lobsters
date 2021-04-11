package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LobstersPostDetails(
  @Json(name = "short_id") val shortId: String,
  @Json(name = "short_id_url") val shortIdUrl: String,
  @Json(name = "created_at") val createdAt: String,
  val title: String,
  val url: String,
  val score: Long,
  val flags: Long,
  @Json(name = "comment_count") val commentCount: Long,
  val description: String,
  @Json(name = "comments_url") val commentsUrl: String,
  @Json(name = "submitter_user") val submitter: User,
  val tags: List<String>,
  val comments: List<Comment>,
)
