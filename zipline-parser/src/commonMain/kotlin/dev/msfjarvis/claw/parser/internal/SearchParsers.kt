/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.parser.model.LobstersPost

private const val BASE_URL = "https://lobste.rs"

internal fun parseSearchResults(html: String): List<LobstersPost> {
  return Ksoup.parse(html, baseUri = BASE_URL)
    .select("div.story_liner.h-entry")
    .map(::parseSearchPost)
}

private fun parseSearchPost(elem: Element): LobstersPost {
  val parent = elem.parent() ?: error("$elem must have a parent")
  val titleElement = elem.select("span.link.h-cite > a")
  val linkElement = elem.select("span.comments_label a")
  val commentCount = linkElement.text().trimStart().substringBefore(" ").toIntOrNull() ?: 0
  return LobstersPost(
    shortId = parent.attr("data-shortid"),
    title = titleElement.text(),
    url = titleElement.attr("abs:href"),
    commentCount = commentCount,
    commentsUrl = BASE_URL + linkElement.attr("href"),
    tags = elem.select("span.tags > a").map(Element::text),
    submitter =
      elem.select("div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])").text(),
    createdAt = "",
    description = "",
    userIsAuthor =
      (elem.select("div.byline > span").firstOrNull()?.text() ?: "").contains(
        "authored",
        ignoreCase = true,
      ),
  )
}
