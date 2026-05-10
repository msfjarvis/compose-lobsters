/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import dev.burnoo.kspoon.annotation.Selector
import dev.drewhamilton.poko.Poko
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
class Tag(
  @Selector("> a.tag") val tag: String,
  @Selector("> span:not(.byline)") val description: String,
  @Selector(":root", attr = "data-privileged", defValue = "false") val privileged: Boolean = false,
  @Serializable(with = ActiveTagSerializer::class)
  @Selector("> span:not(.byline)", attr = "class", defValue = "")
  val active: Boolean = true,
  @Selector(":root", attr = "data-category", defValue = "") val category: String = "",
  @Serializable(with = MediaTagSerializer::class)
  @Selector("> a.tag", attr = "class", defValue = "")
  @SerialName("is_media")
  val isMedia: Boolean = false,
  @Serializable(with = HotnessModSerializer::class)
  @Selector(":root", attr = "data-hotness-mod", defValue = "0.0")
  @SerialName("hotness_mod")
  val hotnessMod: Double = 0.0,
)

internal object ActiveTagSerializer : KSerializer<Boolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("ActiveTag", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): Boolean =
    !decoder.decodeString().split(Regex("\\s+")).contains("inactive_tag")

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}

internal object MediaTagSerializer : KSerializer<Boolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("MediaTag", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): Boolean =
    decoder.decodeString().split(Regex("\\s+")).contains("tag_is_media")

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}

internal object HotnessModSerializer : KSerializer<Double> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("HotnessMod", PrimitiveKind.DOUBLE)

  override fun deserialize(decoder: Decoder): Double =
    decoder.decodeString().toDoubleOrNull() ?: 0.0

  override fun serialize(encoder: Encoder, value: Double) = encoder.encodeDouble(value)
}
