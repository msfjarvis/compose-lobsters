package dev.msfjarvis.lobsters.util

import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.model.LobstersPost

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
