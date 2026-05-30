/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.model.LobstersPost

private const val BASE_URL = "https://lobste.rs"
private val commentCountRegex = "\\d+".toRegex()

internal fun parsePostsPage(html: String): List<LobstersPost> {
  return Ksoup.parse(html, baseUri = BASE_URL).select("li.story").map(::parsePost)
}

private fun parsePost(element: Element): LobstersPost {
  val titleElement = element.select("> div.story_liner span.link.h-cite > a")
  val commentElement = element.select("> div.story_liner span.comments_label a")
  val submitterElement =
    element.select(
      "> div.story_liner div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"
    )

  val timestampElement = element.select("> div.story_liner div.byline > time")
  val descriptionElement = element.select("> div.story_liner a.description_present")
  val tags = element.select("> div.story_liner span.tags > a").map(Element::text)

  return LobstersPost(
    shortId = element.attr("data-shortid"),
    createdAt = normalizeCreatedAt(timestampElement.attr("data-at-unix")),
    title = titleElement.text(),
    url = titleElement.attr("abs:href"),
    description = descriptionElement.attr("title"),
    commentCount = commentCountRegex.find(commentElement.text())?.value?.toInt() ?: 0,
    commentsUrl = commentElement.attr("abs:href"),
    submitter = submitterElement.text(),
    userIsAuthor = submitterElement.attr("class").split(' ').contains("user_is_author"),
    tags = tags,
  )
}
