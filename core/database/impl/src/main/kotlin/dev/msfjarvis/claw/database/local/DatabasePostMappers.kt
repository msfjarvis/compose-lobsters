/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import dev.msfjarvis.claw.model.Discussion
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.SourceStory
import dev.msfjarvis.claw.model.UIPost

fun UIPost.Companion.fromSavedPost(post: SavedPost): UIPost =
  UIPost(
    discussion = Discussion(stories = listOf(post.toSourceStory())),
    description = post.description,
    commentCount = post.commentCount ?: 0,
    commentsUrl = post.commentsUrl,
  )

fun UIPost.Companion.fromCachedRemotePost(post: CachedRemotePost): UIPost =
  UIPost(
    discussion = Discussion(stories = listOf(post.toSourceStory())),
    description = post.description,
    commentCount = post.commentCount ?: 0,
    commentsUrl = post.commentsUrl,
  )

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

private fun SavedPost.toSourceStory() =
  SourceStory(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    submitter = submitterName,
    tags = tags,
    userIsAuthor = userIsAuthor,
  )

private fun CachedRemotePost.toSourceStory() =
  SourceStory(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    submitter = submitterName,
    tags = tags,
    userIsAuthor = userIsAuthor,
  )
