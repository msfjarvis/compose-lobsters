/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.model

import kotlin.jvm.JvmInline

data class LobstersPost(
  val shortId: String,
  val createdAt: String = "",
  val title: String,
  val url: String = "",
  val description: String = "",
  val commentCount: Int = 0,
  val commentsUrl: String = "",
  val submitter: String,
  val userIsAuthor: Boolean = false,
  val tags: List<String> = emptyList(),
)

data class LobstersPostDetails(
  val shortId: String,
  val createdAt: String = "",
  val title: String,
  val url: String = "",
  val description: String = "",
  val commentCount: Int = 0,
  val commentsUrl: String = "",
  val submitter: String,
  val tags: List<String> = emptyList(),
  val comments: List<Comment> = emptyList(),
  val userIsAuthor: Boolean = false,
)

data class Comment(
  val shortId: String,
  val comment: String,
  val url: String = "",
  val score: Int = 1,
  val timestamp: Long,
  val edited: Boolean = false,
  val parentComment: String? = null,
  val user: String = "",
  val isUpvoted: Boolean = false,
)

data class User(
  val username: String,
  val about: String = "",
  val invitedBy: String? = null,
  val avatarUrl: String = "",
  val createdAt: String = "",
)

data class Tag(
  val tag: String,
  val description: String,
  val privileged: Boolean = false,
  val active: Boolean = true,
  val category: String = "",
  val isMedia: Boolean = false,
  val hotnessMod: Double = 0.0,
)

@JvmInline value class CSRFToken(val value: String)

data class ReplyForm(
  val authenticityToken: String,
  val storyId: String,
  val method: String,
  val parentCommentShortId: String,
)
