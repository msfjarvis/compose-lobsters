@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

class ExtendedPostDetails(
  val title: String,
  val linkMetadata: LinkMetadata,
  val description: String,
  val submitter: User,
  val tags: List<String>,
  val comments: List<Comment>,
  val commentsUrl: String,
)
