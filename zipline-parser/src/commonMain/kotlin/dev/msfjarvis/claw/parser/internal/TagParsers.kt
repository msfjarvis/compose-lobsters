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
  return Tag(
    tag = element.select("> a.tag").text(),
    description = element.select("> span:not(.byline)").text(),
    privileged = element.attr("data-privileged").toBoolean(),
    active =
      !element
        .select("> span:not(.byline)")
        .attr("class")
        .split(Regex("\\s+"))
        .contains("inactive_tag"),
    category = element.attr("data-category"),
    isMedia = element.select("> a.tag").attr("class").split(Regex("\\s+")).contains("tag_is_media"),
    hotnessMod = element.attr("data-hotness-mod").toDoubleOrNull() ?: 0.0,
  )
}
