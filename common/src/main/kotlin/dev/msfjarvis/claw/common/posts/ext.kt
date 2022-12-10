/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
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
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = submitter.username,
    submitterAvatarUrl = submitter.avatarUrl,
    tags = tags,
    description = description,
  )
}

fun LobstersPostDetails.toDbModel(): SavedPost {
  return SavedPost(
    shortId = shortId,
    title = title,
    url = url,
    createdAt = createdAt,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = submitter.username,
    submitterAvatarUrl = submitter.avatarUrl,
    tags = tags,
    description = description,
  )
}
