package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.serialization.JavaInstantSerializer
import java.time.temporal.TemporalAccessor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Comment(
  val comment: String,
  val url: String,
  val score: Int,
  @Serializable(with = JavaInstantSerializer::class)
  @SerialName("created_at")
  val createdAt: TemporalAccessor,
  @Serializable(with = JavaInstantSerializer::class)
  @SerialName("updated_at")
  val updatedAt: TemporalAccessor,
  @SerialName("indent_level") val indentLevel: Int,
  @SerialName("commenting_user") val user: User,
)
