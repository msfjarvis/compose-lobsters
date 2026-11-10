/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

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
