package dev.msfjarvis.lobsters.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(
  tableName = "lobsters_posts"
)
@JsonClass(generateAdapter = true)
class LobstersPost(
  @Json(name = "short_id")
  @PrimaryKey
  val shortId: String,
  @Json(name = "short_id_url")
  val shortIdUrl: String,
  @Json(name = "created_at")
  val createdAt: String,
  val title: String,
  val url: String,
  val score: Long,
  val flags: Long,
  @Json(name = "comment_count")
  val commentCount: Long,
  val description: String,
  @Json(name = "comments_url")
  val commentsUrl: String,
  @Json(name = "submitter_user")
  val submitterUser: Submitter,
  val tags: List<String>
)
