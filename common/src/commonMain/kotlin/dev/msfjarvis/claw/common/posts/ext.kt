package dev.msfjarvis.claw.common.posts

import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails

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

fun LobstersPostDetails.toDbModel(): SavedPost {
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
