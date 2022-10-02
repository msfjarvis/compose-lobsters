@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class User(
  val username: String,
  val about: String,
  @SerialName("invited_by_user") val invitedBy: String,
  @SerialName("avatar_url") val avatarUrl: String,
  @SerialName("created_at") val createdAt: String,
)
