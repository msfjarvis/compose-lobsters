/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.model.FiltersPage
import dev.msfjarvis.claw.model.Tag

internal fun parseTagsPage(html: String): List<Tag> {
  return Ksoup.parse(html, baseUri = BASE_URL).select("tr:has(a.tag)").mapNotNull(::parseTagRow)
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
    document.select("tr:has(input[type=checkbox][name^='tags['])").mapNotNull(::parseTagRow)

  return FiltersPage(authenticityToken = authenticityToken, tags = tags, blockedTags = blockedTags)
}

private fun parseTagRow(row: Element): Tag? {
  val tagElement = row.select("a.tag").first() ?: return null
  val descriptionElement = row.select("label span").first() ?: return null
  return tag(
    tagElement = tagElement,
    descriptionElement = descriptionElement,
    element = row,
  )
}

private fun tag(tagElement: Element, descriptionElement: Element, element: Element): Tag {
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
