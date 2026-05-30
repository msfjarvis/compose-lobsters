/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.internal

import com.fleeksoft.ksoup.nodes.Element
import dev.msfjarvis.claw.model.Comment
import kotlin.time.Instant

internal fun parseComments(root: Element): List<Comment> {
  val elements = root.select("ol.comments > li.comments_subtree")
  val seen = mutableSetOf<String>()
  return buildList {
    elements.forEach { subtree -> addSubtree(subtree, parentComment = null, seen) }
  }
}

private fun MutableList<Comment>.addSubtree(
  subtree: Element,
  parentComment: String?,
  seen: MutableSet<String>,
) {
  val commentElement =
    if (subtree.`is`("div.comment")) subtree
    else subtree.children().firstOrNull { it.`is`("div.comment") } ?: return
  val shortId = commentElement.attr("data-shortid")
  if (!seen.add(shortId)) return
  val comment = commentElement.toComment(parentComment)
  add(comment)
  val childContainer = if (subtree.`is`("div.comment")) subtree.parent() ?: subtree else subtree
  childContainer
    .children()
    .filter { it.`is`("ol.comments") }
    .flatMap { comments -> comments.children().filter { it.`is`("li.comments_subtree") } }
    .forEach { child -> addSubtree(child, parentComment = comment.shortId, seen) }
}

private fun Element.toComment(parentComment: String?): Comment {
  val byline = selectFirst("div.byline")
  val timestamp = byline?.selectFirst("a[href^=/c/] time")?.attr("data-at-unix").orEmpty()
  val isEdited = byline?.text()?.contains("edited") == true
  return Comment(
    shortId = attr("data-shortid"),
    comment = selectFirst("div.comment_text")?.html().orEmpty(),
    url = selectFirst("div.byline a[href^=/c/]")?.absUrl("href").orEmpty(),
    score =
      children()
        .firstOrNull { it.hasClass("voters") }
        ?.children()
        ?.firstOrNull { it.hasClass("upvoter") }
        ?.text()
        ?.trim()
        ?.takeUnless { it == "~" }
        ?.toIntOrNull() ?: 1,
    timestamp = timestamp.toInstant(),
    edited = isEdited,
    parentComment = parentComment,
    user =
      getElementsByClass("byline")
        .flatMap { it.getElementsByTag("a") }
        .firstOrNull { it.attr("href").contains("/~") && it.text().isNotBlank() }
        ?.text()
        .orEmpty(),
    isUpvoted = classNames().contains("upvoted"),
  )
}

private fun String.toInstant(): Instant {
  return when {
    isBlank() -> Instant.fromEpochSeconds(0)
    all(Char::isDigit) -> Instant.fromEpochSeconds(toLong())
    else -> Instant.parse(this)
  }
}
