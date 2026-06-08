/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.parser.model.LobstersPostDetails

private val commentCountRegex by lazy(LazyThreadSafetyMode.NONE) { "\\d+".toRegex() }
private const val STORY_SELECTOR = "ol.stories > li.story"
private const val SUBMITTER_SELECTOR =
  "ol.stories > li.story div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"

internal fun parsePostDetails(html: String): LobstersPostDetails {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  val storyElement = document.select(STORY_SELECTOR)
  val timestampElement = document.select("$STORY_SELECTOR div.byline > time")
  val titleElement = document.select("$STORY_SELECTOR span.link.h-cite > a")
  val commentsElement = document.select("$STORY_SELECTOR span.comments_label a")
  val submitterElement = document.select(SUBMITTER_SELECTOR)
  val tags = document.select("$STORY_SELECTOR span.tags > a").map { it.text() }
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
    userIsAuthor = submitterElement.attr("class").split(' ').contains("user_is_author"),
  )
}
