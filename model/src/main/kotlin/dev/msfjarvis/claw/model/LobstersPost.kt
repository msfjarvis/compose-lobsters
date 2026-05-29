/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
@file:Suppress("LongParameterList")

package dev.msfjarvis.claw.model

import dev.burnoo.kspoon.annotation.Selector
import dev.drewhamilton.poko.Poko
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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
@KonvertTo(
  value = UIPost::class,
  mappings = [Mapping(target = "submitter", expression = "it.submitter")],
)
class LobstersPost(
  @Selector(":root", attr = "data-shortid") val shortId: String,
  @Serializable(with = CreatedAtSerializer::class)
  @Selector("> div.story_liner div.byline > time", attr = "data-at-unix", defValue = "")
  val createdAt: String = "",
  @Selector("> div.story_liner span.link.h-cite > a") val title: String,
  @Selector("> div.story_liner span.link.h-cite > a", attr = "abs:href", defValue = "")
  val url: String = "",
  @Selector("> div.story_liner a.description_present", attr = "title", defValue = "")
  val description: String = "",
  @Serializable(with = CommentCountSerializer::class)
  @Selector(
    "> div.story_liner span.comments_label a",
    regex = "(\\d+ comments?|no comments)",
    defValue = "0",
  )
  val commentCount: Int = 0,
  @Selector("> div.story_liner span.comments_label a", attr = "abs:href", defValue = "")
  val commentsUrl: String = "",
  @Selector("> div.story_liner div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])")
  @SerialName("submitter_user")
  val submitter: String,
  @Serializable(with = UserIsAuthorSerializer::class)
  @Selector(
    "> div.story_liner div.byline > a[href^=/~]:not([tabindex]):not([aria-hidden=true])",
    attr = "class",
    defValue = "",
  )
  @SerialName("user_is_author")
  val userIsAuthor: Boolean = false,
  @Selector("> div.story_liner span.tags > a") val tags: List<String> = emptyList(),
)

internal object CreatedAtSerializer : KSerializer<String> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CreatedAt", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): String {
    val value = decoder.decodeString()
    return when {
      value.isBlank() -> ""
      value.all(Char::isDigit) ->
        value.toLongOrNull()?.let {
          Instant.ofEpochSecond(it)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } ?: ""
      runCatching { DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value) }.isSuccess -> value
      else ->
        runCatching {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
              .atOffset(ZoneOffset.UTC)
              .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
          }
          .getOrDefault("")
    }
  }

  override fun serialize(encoder: Encoder, value: String) = encoder.encodeString(value)
}

internal object CommentCountSerializer : KSerializer<Int> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CommentCount", PrimitiveKind.INT)

  override fun deserialize(decoder: Decoder): Int {
    return "\\d+".toRegex().find(decoder.decodeString())?.value?.toInt() ?: 0
  }

  override fun serialize(encoder: Encoder, value: Int) = encoder.encodeInt(value)
}

internal object UserIsAuthorSerializer : KSerializer<Boolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("UserIsAuthor", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): Boolean =
    decoder.decodeString().split(' ').contains("user_is_author")

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}
