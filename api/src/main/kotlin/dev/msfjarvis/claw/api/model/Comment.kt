package dev.msfjarvis.claw.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Comment(
  @Json(name = "short_id") val shortId: String,
  @Json(name = "short_id_url") val shortIdUrl: String,
  @Json(name = "created_at") val createdAt: String,
  @Json(name = "updated_at") val updatedAt: String,
  @Json(name = "is_deleted") val isDeleted: Boolean,
  @Json(name = "is_moderated") val isModerated: Boolean,
  val score: Long,
  val flags: Long,
  val comment: String,
  val url: String,
  @Json(name = "indent_level") val indentLevel: Long,
  @Json(name = "commenting_user") val user: User,
)
