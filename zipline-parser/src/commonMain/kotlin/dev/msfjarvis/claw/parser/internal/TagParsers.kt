/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.model.Tag

private const val BASE_URL = "https://lobste.rs"

internal fun parseTagsPage(html: String): List<Tag> {
  return Ksoup.parse(html, baseUri = BASE_URL).select("ol.category_tags > li").map(::parseTag)
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
