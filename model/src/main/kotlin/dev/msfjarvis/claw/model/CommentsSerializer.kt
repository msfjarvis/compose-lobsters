/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import com.fleeksoft.ksoup.nodes.Element
import dev.burnoo.kspoon.decoder.KspoonDecoder
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(InternalSerializationApi::class)
internal object CommentsSerializer : KSerializer<List<Comment>> {
  override val descriptor: SerialDescriptor = buildSerialDescriptor("Comments", StructureKind.LIST)

  override fun deserialize(decoder: Decoder): List<Comment> {
    val elements = (decoder as KspoonDecoder).decodeElements()
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
    val timestamp = selectFirst("div.byline a[href^=/c/] time")?.attr("data-at-unix").orEmpty()
    val parsedTimestamp = timestamp.toTemporalAccessor()
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
          ?.toIntOrNull() ?: 0,
      createdAt = parsedTimestamp,
      lastEditedAt = parsedTimestamp,
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

  private fun String.toTemporalAccessor(): TemporalAccessor {
    val isoValue =
      if (isBlank()) {
        Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      } else if (all(Char::isDigit)) {
        Instant.ofEpochSecond(toLong())
          .atOffset(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      } else {
        this
      }
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(isoValue)
  }

  override fun serialize(encoder: Encoder, value: List<Comment>) =
    throw UnsupportedOperationException()
}
