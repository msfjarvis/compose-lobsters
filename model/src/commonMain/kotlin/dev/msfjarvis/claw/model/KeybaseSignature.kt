package dev.msfjarvis.claw.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class KeybaseSignature(
  @SerialName("kb_username") val kbUsername: String,
  @SerialName("sig_hash") val sigHash: String,
)
