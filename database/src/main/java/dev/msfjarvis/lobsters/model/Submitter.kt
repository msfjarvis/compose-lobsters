package dev.msfjarvis.lobsters.model

import com.squareup.moshi.Json

class Submitter(
  val username: String,
  @Json(name = "created_at")
  val createdAt: String,
  @Json(name = "is_admin")
  val isAdmin: Boolean,
  val about: String,
  @Json(name = "is_moderator")
  val isModerator: Boolean,
  val karma: Long = 0,
  @Json(name = "avatar_url")
  val avatarUrl: String,
  @Json(name = "invited_by_user")
  val invitedByUser: String,
  @Json(name = "github_username")
  val githubUsername: String? = null,
  @Json(name = "twitter_username")
  val twitterUsername: String? = null,
  @Json(name = "keybase_signatures")
  val keybaseSignatures: List<KeybaseSignature> = emptyList()
)
