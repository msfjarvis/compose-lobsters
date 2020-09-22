package dev.msfjarvis.lobsters.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Submitter(
  val username: String,
  @Json(name = "created_at")
  val createdAt: String,
  @Json(name = "is_admin")
  val isAdmin: Boolean,
  val about: String,
  @Json(name = "is_moderator")
  val isModerator: Boolean,
  val karma: Long,
  @Json(name = "avatar_url")
  val avatarUrl: String,
  @Json(name = "invited_by_user")
  val invitedByUser: String,
  @Json(name = "github_username")
  val githubUsername: String?,
  @Json(name = "twitter_username")
  val twitterUsername: String?,
  @Json(name = "keybase_signatures")
  val keybaseSignatures: List<KeybaseSignature>?
)
