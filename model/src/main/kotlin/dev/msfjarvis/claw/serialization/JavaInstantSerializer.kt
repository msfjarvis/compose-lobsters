package dev.msfjarvis.claw.serialization

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class JavaInstantSerializer : KSerializer<TemporalAccessor> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): TemporalAccessor {
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: TemporalAccessor) =
    encoder.encodeString(value.toString())
}
