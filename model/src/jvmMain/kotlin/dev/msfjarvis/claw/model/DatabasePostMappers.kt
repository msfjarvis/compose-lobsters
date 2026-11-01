/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.msfjarvis.claw.database.local.CachedRemotePost
import dev.msfjarvis.claw.database.local.SavedPost

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
