package dev.msfjarvis.lobsters.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
class KeybaseSignature(
  @SerialName("kb_username")
  val kbUsername: String,
  @SerialName("sig_hash")
  val sigHash: String
): Metadata {
  override val id: Any
    get() = kbUsername
}
