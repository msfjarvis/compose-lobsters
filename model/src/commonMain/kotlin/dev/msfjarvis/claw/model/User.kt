package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class User(
  val username: String,
  @SerialName("avatar_url") val avatarUrl: String,
)
