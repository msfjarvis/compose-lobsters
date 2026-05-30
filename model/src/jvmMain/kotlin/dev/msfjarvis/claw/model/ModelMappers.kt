/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.database.local.SavedPost

fun LobstersPost.toUIPost(): UIPost =
  UIPost(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitter = submitter,
    tags = tags,
    userIsAuthor = userIsAuthor,
  )

fun LobstersPostDetails.toUIPost(): UIPost =
  UIPost(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitter = submitter,
    tags = tags,
    comments = comments,
    userIsAuthor = userIsAuthor,
  )

fun LobstersPostDetails.toSavedPost(): SavedPost =
  SavedPost(
    shortId = shortId,
    title = title,
    url = url,
    createdAt = createdAt,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = submitter,
    tags = tags,
    description = description,
    userIsAuthor = userIsAuthor,
  )

fun UIPost.toSavedPost(): SavedPost =
  SavedPost(
    shortId = shortId,
    title = title,
    url = url,
    createdAt = createdAt,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = submitter,
    tags = tags,
    description = description,
    userIsAuthor = userIsAuthor,
  )
