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
import dev.msfjarvis.claw.model.LobstersPostDetails

private val commentCountRegex by lazy(LazyThreadSafetyMode.NONE) { "\\d+".toRegex() }
private val sourceStoryIdRegex by lazy(LazyThreadSafetyMode.NONE) { Regex("#comments-([A-Za-z0-9]+)") }
private const val STORY_SELECTOR = "ol.stories > li.story"
private const val SUBMITTER_SELECTOR =
  "div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])"

internal fun parsePostDetails(html: String): LobstersPostDetails {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  val storyElement =
    requireNotNull(document.selectFirst(STORY_SELECTOR)) { "Post details have no story" }
  val primaryId = storyElement.attr("data-shortid")
  val primaryDescription = document.selectFirst("div.story_content div.story_text")?.html().orEmpty()
  val sourceHeaders = storyElement.select("div.details[id^=header_story_]")
  val sources =
    sourceHeaders
      .mapIndexed { index, header ->
        val sourceId =
          if (index == 0) primaryId
          else
            requireNotNull(sourceStoryId(document, header)) {
              "Merged source header has no comment section"
            }
        header.toPost(sourceId, if (index == 0) primaryDescription else "")
      }
      .ifEmpty {
        listOf(
          (storyElement.selectFirst("div.details") ?: storyElement)
            .toPost(primaryId, primaryDescription)
        )
      }
  val commentCount =
    commentCountRegex
      .find(storyElement.selectFirst("span.comments_label")?.text().orEmpty())
      ?.value
      ?.toIntOrNull() ?: 0
  val canonicalUrl =
    document.selectFirst("link[rev=canonical][href]")?.attr("abs:href")
      ?.takeIf { it.startsWith("$BASE_URL/s/$primaryId/") } ?: "$BASE_URL/s/$primaryId/c"
  val comments =
    sources.flatMap { source ->
      val section = document.selectFirst("li.comments_subtree[id=comments-${source.shortId}]")
      val container = section?.selectFirst("ol#story_comments")
      if (container != null) parseComments(container, source.shortId)
      else if (sources.size == 1) parseComments(document.selectFirst("ol.comments") ?: document, source.shortId)
      else throw IllegalStateException("Merged source ${source.shortId} has no comment container")
    }
  return LobstersPostDetails(
    stories = sources,
    comments = comments,
    commentsUrl = canonicalUrl,
    commentCount = commentCount,
  )
}

private fun sourceStoryId(document: Element, header: Element): String? {
  val suffix = header.id().removePrefix("header_story_")
  val sourceHeading = document.selectFirst("div.details#comments_story_$suffix") ?: return null
  return sourceStoryIdRegex
    .find(sourceHeading.selectFirst("a[href^=#comments-]")?.attr("href").orEmpty())
    ?.groupValues
    ?.get(1)
}

private fun Element.toPost(shortId: String, description: String): LobstersPost {
  val titleElement = selectFirst("span.link.h-cite > a")
  val submitter = select(SUBMITTER_SELECTOR).firstOrNull()
  val commentsCount =
    commentCountRegex.find(selectFirst("span.comments_label")?.text().orEmpty())?.value?.toIntOrNull()
      ?: 0
  return LobstersPost(
    shortId = shortId,
    createdAt = normalizeCreatedAt(selectFirst("div.byline > time")?.attr("data-at-unix").orEmpty()),
    title = titleElement?.text().orEmpty(),
    url = titleElement?.attr("abs:href").orEmpty(),
    description = description,
    commentCount = commentsCount,
    commentsUrl = titleElement?.attr("abs:href").orEmpty(),
    submitter = submitter?.text().orEmpty(),
    tags = select(".tags a").map { it.text() },
    userIsAuthor = select(".user_is_author, .user_is_submitter").isNotEmpty(),
  )
}
