/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object CommentEditedSerializer : KSerializer<Boolean> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("CommentEdited", PrimitiveKind.BOOLEAN)

  override fun deserialize(decoder: Decoder): Boolean = decoder.decodeString().contains("edited")

  override fun serialize(encoder: Encoder, value: Boolean) = encoder.encodeBoolean(value)
}
