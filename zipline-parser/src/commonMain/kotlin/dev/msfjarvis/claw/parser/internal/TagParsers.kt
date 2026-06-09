/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.parser.model.FiltersPage
import dev.msfjarvis.claw.parser.model.Tag

internal fun parseTagsPage(html: String): List<Tag> {
  return Ksoup.parse(html, baseUri = BASE_URL).select("ol.category_tags > li").map(::parseTag)
}

internal fun parseFiltersPage(html: String): FiltersPage {
  val document = Ksoup.parse(html, baseUri = BASE_URL)
  val authenticityToken =
    document.select("input[name=authenticity_token]").firstOrNull()?.attr("value").orEmpty()
  val blockedTags =
    document
      .select("input[type=checkbox][name^='tags[']")
      .filter { it.hasAttr("checked") }
      .mapNotNull {
        it.attr("name").removePrefix("tags[").removeSuffix("]").takeIf(String::isNotBlank)
      }
      .toSet()
  val tags =
    document.select("tr:has(input[type=checkbox][name^='tags['])").mapNotNull { row ->
      val link = row.select("a.tag").firstOrNull() ?: return@mapNotNull null
      val description = row.select("label span").firstOrNull()?.text().orEmpty()
      Tag(
        tag = link.text(),
        description = description,
        privileged = row.attr("data-privileged").toBoolean(),
        active =
          !row.select("label span").firstOrNull()?.classNames().orEmpty().contains("inactive_tag"),
        category = row.attr("data-category"),
        isMedia = link.classNames().contains("tag_is_media"),
        hotnessMod = row.attr("data-hotness-mod").toDoubleOrNull() ?: 0.0,
      )
    }

  return FiltersPage(authenticityToken = authenticityToken, tags = tags, blockedTags = blockedTags)
}

private fun parseTag(element: Element): Tag {
  val tagElement = element.select("> a.tag")
  val descriptionElement = element.select("> span:not(.byline)")
  return Tag(
    tag = tagElement.text(),
    description = descriptionElement.text(),
    privileged = element.attr("data-privileged").toBoolean(),
    active = !descriptionElement.hasClass("inactive_tag"),
    category = element.attr("data-category"),
    isMedia = tagElement.hasClass("tag_is_media"),
    hotnessMod = element.attr("data-hotness-mod").toDoubleOrNull() ?: 0.0,
  )
}
