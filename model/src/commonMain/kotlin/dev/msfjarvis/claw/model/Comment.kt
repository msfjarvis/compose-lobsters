package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Comment(
  @SerialName("short_id") val shortId: String,
  @SerialName("short_id_url") val shortIdUrl: String,
  @SerialName("created_at") val createdAt: String,
  @SerialName("updated_at") val updatedAt: String,
  @SerialName("is_deleted") val isDeleted: Boolean,
  @SerialName("is_moderated") val isModerated: Boolean,
  val score: Long,
  val flags: Long,
  val comment: String,
  val url: String,
  @SerialName("indent_level") val indentLevel: Long,
  @SerialName("commenting_user") val user: User,
)
