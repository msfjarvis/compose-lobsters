/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import dev.msfjarvis.claw.model.LobstersPostDetails

private const val BASE_URL = "https://lobste.rs"

internal fun parsePostDetails(html: String): LobstersPostDetails {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  return LobstersPostDetails(
    shortId = document.select("ol.stories > li.story").attr("data-shortid"),
    createdAt =
      normalizeCreatedAt(
        document.select("ol.stories > li.story div.byline > time").attr("data-at-unix")
      ),
    title = document.select("ol.stories > li.story span.link.h-cite > a").text(),
    url = document.select("ol.stories > li.story span.link.h-cite > a").attr("abs:href"),
    description = document.select("div.story_content div.story_text").html(),
    commentCount =
      "\\d+"
        .toRegex()
        .find(document.select("ol.stories > li.story span.comments_label a").text())
        ?.value
        ?.toInt() ?: 0,
    commentsUrl = document.select("ol.stories > li.story span.comments_label a").attr("abs:href"),
    submitter =
      document
        .select(
          "ol.stories > li.story div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"
        )
        .text(),
    tags = document.select("ol.stories > li.story span.tags > a").map { it.text() },
    comments = parseComments(document),
    userIsAuthor =
      document
        .select(
          "ol.stories > li.story div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"
        )
        .attr("class")
        .split(' ')
        .contains("user_is_author"),
  )
}
