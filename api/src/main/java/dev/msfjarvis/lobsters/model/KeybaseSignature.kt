package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Json

class KeybaseSignature(
  @Json(name = "kb_username")
  val kbUsername: String,
  @Json(name = "sig_hash")
  val sigHash: String,
)
