package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Comment(
  val comment: String,
  val url: String,
  @SerialName("indent_level") val indentLevel: Int,
  @SerialName("commenting_user") val user: User,
)
