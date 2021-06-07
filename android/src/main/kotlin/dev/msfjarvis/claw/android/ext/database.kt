package dev.msfjarvis.claw.android.ext

import dev.msfjarvis.claw.api.model.LobstersPost
import dev.msfjarvis.claw.database.local.SavedPost

/** Convert a [LobstersPost] object returned by the API into a [SavedPost] for persistence. */
fun LobstersPost.toDbModel(): SavedPost {
  return SavedPost(
    shortId = shortId,
    title = title,
    url = url,
    createdAt = createdAt,
    commentsUrl = commentsUrl,
    submitterName = submitter.username,
    submitterAvatarUrl = submitter.avatarUrl,
    tags = tags,
  )
}
