/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.api.converters

import dev.msfjarvis.claw.model.CSRFToken
import dev.msfjarvis.claw.model.Comment
import dev.msfjarvis.claw.model.LobstersPost
import dev.msfjarvis.claw.model.LobstersPostDetails
import dev.msfjarvis.claw.model.ReplyForm
import dev.msfjarvis.claw.model.Tag
import dev.msfjarvis.claw.model.User

internal fun dev.msfjarvis.claw.parser.model.CSRFToken.toModel(): CSRFToken = CSRFToken(value)

internal fun dev.msfjarvis.claw.parser.model.LobstersPost.toModel(): LobstersPost =
  LobstersPost(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitter = submitter,
    userIsAuthor = userIsAuthor,
    tags = tags,
  )

internal fun dev.msfjarvis.claw.parser.model.LobstersPostDetails.toModel(): LobstersPostDetails =
  LobstersPostDetails(
    shortId = shortId,
    createdAt = createdAt,
    title = title,
    url = url,
    description = description,
    commentCount = commentCount,
    commentsUrl = commentsUrl,
    submitter = submitter,
    tags = tags,
    comments = comments.map { it.toModel() },
    userIsAuthor = userIsAuthor,
  )

internal fun dev.msfjarvis.claw.parser.model.Comment.toModel(): Comment =
  Comment(
    shortId = shortId,
    comment = comment,
    url = url,
    score = score,
    timestamp = timestamp,
    edited = edited,
    parentComment = parentComment,
    user = user,
    isUpvoted = isUpvoted,
  )

internal fun dev.msfjarvis.claw.parser.model.ReplyForm.toModel(): ReplyForm =
  ReplyForm(
    authenticityToken = authenticityToken,
    storyId = storyId,
    method = method,
    parentCommentShortId = parentCommentShortId,
  )

internal fun dev.msfjarvis.claw.parser.model.Tag.toModel(): Tag =
  Tag(
    tag = tag,
    description = description,
    privileged = privileged,
    active = active,
    category = category,
    isMedia = isMedia,
    hotnessMod = hotnessMod,
  )

internal fun dev.msfjarvis.claw.parser.model.User.toModel(): User =
  User(
    username = username,
    about = about,
    invitedBy = invitedBy,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
  )
