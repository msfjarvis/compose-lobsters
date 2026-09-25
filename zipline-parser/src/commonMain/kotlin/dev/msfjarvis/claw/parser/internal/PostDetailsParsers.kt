/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.model.LobstersPostDetails

private val commentCountRegex by lazy(LazyThreadSafetyMode.NONE) { "\\d+".toRegex() }
private const val STORY_SELECTOR = "ol.stories > li.story"
private const val SUBMITTER_SELECTOR =
  "div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"

internal fun parsePostDetails(html: String): LobstersPostDetails {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  val storyElement = checkNotNull(document.selectFirst(STORY_SELECTOR)) { "Story not found" }
  val timestampElement = storyElement.select("div.byline > time")
  val titleElement = storyElement.select("span.link.h-cite > a")
  val commentsElement = storyElement.select("span.comments_label a")
  val submitterElement = storyElement.select(SUBMITTER_SELECTOR)
  val tags = storyElement.select(".tags a").map { it.text() }
  return LobstersPostDetails(
    shortId = storyElement.attr("data-shortid"),
    createdAt = normalizeCreatedAt(timestampElement.attr("data-at-unix")),
    title = titleElement.text(),
    url = titleElement.attr("abs:href"),
    description = document.select("div.story_content div.story_text").html(),
    commentCount = commentCountRegex.find(commentsElement.text())?.value?.toInt() ?: 0,
    commentsUrl = commentsElement.attr("abs:href"),
    submitter = submitterElement.text(),
    tags = tags,
    comments = parseComments(document),
    userIsAuthor =
      storyElement.select(".user_is_author, .user_is_submitter").isNotEmpty() ||
        document.select("ol.comments .user_is_author, ol.comments .user_is_submitter").isNotEmpty(),
  )
}
