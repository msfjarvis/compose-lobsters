/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.burnoo.kspoon.SelectorHtmlTextMode
import dev.burnoo.kspoon.annotation.Selector
import dev.drewhamilton.poko.Poko
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Poko
@Selector("div.comment")
class Comment(
  @Selector(":root", attr = "data-shortid") val shortId: String,
  @Selector("div.comment_text", textMode = SelectorHtmlTextMode.InnerHtml) val comment: String,
  @Selector("div.byline a[href^=/c/]", attr = "abs:href", defValue = "") val url: String = "",
  @Serializable(with = CommentScoreSerializer::class)
  @Selector("div.voters a.upvoter", defValue = "1")
  val score: Int = 1,
  @Serializable(with = CommentInstantSerializer::class)
  @Selector("div.byline a[href^=/c/] time", attr = "data-at-unix", defValue = "")
  val timestamp: TemporalAccessor,
  @Serializable(with = CommentEditedSerializer::class)
  @Selector("div.byline span", defValue = "")
  val edited: Boolean = false,
  @Serializable(with = EmptyStringAsNullSerializer::class)
  @Selector(":root", attr = "data-parent-shortid", defValue = "")
  val parentComment: String? = null,
  @Selector("div.byline a[href^=/~/]", defValue = "")
  @SerialName("commenting_user")
  val user: String = "",
  @Serializable(with = CommentUpvotedSerializer::class)
  @Selector(":root", attr = "class", defValue = "")
  val isUpvoted: Boolean = false,
)

internal object CommentInstantSerializer : KSerializer<TemporalAccessor> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CommentInstant", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): TemporalAccessor {
    val value = decoder.decodeString()
    val isoValue =
      if (value.isBlank()) {
        Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      } else if (value.all(Char::isDigit)) {
        Instant.ofEpochSecond(value.toLong())
          .atOffset(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      } else {
        value
      }
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(isoValue)
  }

  override fun serialize(encoder: Encoder, value: TemporalAccessor) =
    encoder.encodeString(value.toString())
}

internal object CommentScoreSerializer : KSerializer<Int> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CommentScore", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): Int =
    decoder.decodeString().trim().takeUnless { it == "~" }?.toIntOrNull() ?: 1

  override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
}

internal object EmptyStringAsNullSerializer : KSerializer<String?> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("EmptyStringAsNull", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): String? = decoder.decodeString().ifBlank { null }

  override fun serialize(encoder: Encoder, value: String?) = encoder.encodeString(value.orEmpty())
}

internal object CommentUpvotedSerializer : KSerializer<Boolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CommentUpvoted", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): Boolean =
    decoder.decodeString().split(Regex("\\s+")).contains("upvoted")

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}
