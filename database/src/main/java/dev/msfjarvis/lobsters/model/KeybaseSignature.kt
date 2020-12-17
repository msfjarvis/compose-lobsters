package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class KeybaseSignature(
  @Json(name = "kb_username")
  val kbUsername: String,
  @Json(name = "sig_hash")
  val sigHash: String
)
