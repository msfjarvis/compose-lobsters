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
    shortId = post.shortId,
    createdAt = post.createdAt,
    title = post.title,
    url = post.url,
    description = post.description,
    commentCount = post.commentCount ?: 0,
    commentsUrl = post.commentsUrl,
    submitter = post.submitterName,
    tags = post.tags,
    userIsAuthor = post.userIsAuthor,
  )

fun UIPost.Companion.fromCachedRemotePost(post: CachedRemotePost): UIPost =
  UIPost(
    shortId = post.shortId,
    createdAt = post.createdAt,
    title = post.title,
    url = post.url,
    description = post.description,
    commentCount = post.commentCount ?: 0,
    commentsUrl = post.commentsUrl,
    submitter = post.submitterName,
    tags = post.tags,
    userIsAuthor = post.userIsAuthor,
  )
