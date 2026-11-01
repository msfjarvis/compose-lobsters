/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

data class SourceStory(
  val shortId: String,
  val createdAt: String,
  val title: String,
  val url: String,
  val submitter: String,
  val tags: List<String>,
  val userIsAuthor: Boolean = false,
)

data class Discussion(
  val stories: List<SourceStory>,
  val comments: List<Comment> = emptyList(),
) {
  init {
    require(stories.isNotEmpty()) { "At least one source story must be part of a discussion" }
    require(stories.map(SourceStory::shortId).toSet().size == stories.size) {
      "Source story IDs must be unique"
    }
    val storyIds = stories.mapTo(mutableSetOf(), SourceStory::shortId)
    require(comments.all { it.sourceStoryId in storyIds }) {
      "Every comment must refer to a source story in the discussion"
    }
  }

  val first: SourceStory
    get() = stories.first()

  val allTags: List<String>
    get() = stories.flatMap(SourceStory::tags)
}

data class UIPost(
  val discussion: Discussion,
  val description: String,
  val commentCount: Int,
  val commentsUrl: String,
) {
  val shortId: String
    get() = discussion.first.shortId

  val createdAt: String
    get() = discussion.first.createdAt

  val title: String
    get() = discussion.first.title

  val url: String
    get() = discussion.first.url

  val submitter: String
    get() = discussion.first.submitter

  val tags: List<String>
    get() = discussion.first.tags

  val userIsAuthor: Boolean
    get() = discussion.first.userIsAuthor

  val comments: List<Comment>
    get() = discussion.comments

  companion object
}
