/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.database.local.SavedPost

fun LobstersPost.toSourceStory(): SourceStory =
  SourceStory(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    submitter = submitter,
    tags = tags,
    userIsAuthor = userIsAuthor,
  )

fun LobstersPost.toUIPost(): UIPost =
  UIPost(
    discussion = Discussion(stories = listOf(toSourceStory())),
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
  )

fun LobstersPostDetails.toUIPost(): UIPost {
  val primary = stories.first()
  return UIPost(
    discussion =
      Discussion(stories = stories.map(LobstersPost::toSourceStory), comments = comments),
    description = primary.description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
  )
}

fun LobstersPostDetails.toSavedPost(): SavedPost {
  val primary = stories.first()
  return SavedPost(
    shortId = primary.shortId,
    title = primary.title,
    url = primary.url,
    createdAt = primary.createdAt,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitterName = primary.submitter,
    tags = primary.tags,
    description = primary.description,
    userIsAuthor = primary.userIsAuthor,
  )
}

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
