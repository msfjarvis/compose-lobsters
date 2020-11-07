package dev.msfjarvis.lobsters.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.kodein.db.model.orm.Metadata

@Serializable
class Submitter(
  val username: String,
  @SerialName("created_at")
  val createdAt: String,
  @SerialName("is_admin")
  val isAdmin: Boolean,
  val about: String,
  @SerialName("is_moderator")
  val isModerator: Boolean,
  val karma: Long = 0,
  @SerialName("avatar_url")
  val avatarUrl: String,
  @SerialName("invited_by_user")
  val invitedByUser: String,
  @SerialName("github_username")
  val githubUsername: String? = null,
  @SerialName("twitter_username")
  val twitterUsername: String? = null,
  @SerialName("keybase_signatures")
  val keybaseSignatures: List<KeybaseSignature> = emptyList()
): Metadata {
  override val id: Any
    get() = listOf(username, githubUsername, twitterUsername)
}
