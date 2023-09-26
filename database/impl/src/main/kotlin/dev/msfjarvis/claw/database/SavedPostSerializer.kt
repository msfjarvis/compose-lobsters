/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database

import dev.msfjarvis.claw.database.local.SavedPost
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

@OptIn(ExperimentalSerializationApi::class)
object SavedPostSerializer : KSerializer<SavedPost> {
  private val delegateSerializer = ListSerializer(String.serializer())
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("SavedPost") {
      element<String>("shortId")
      element<String>("title")
      element<String>("url")
      element<String>("createdAt")
      element<Int?>("commentCount", isOptional = true)
      element<String>("commentsUrl")
      element<String>("submitterName")
      element<String>("submitterAvatarUrl")
      element<List<String>>("tags")
      element<String>("description")
    }

  override fun deserialize(decoder: Decoder): SavedPost {
    return decoder.decodeStructure(descriptor) {
      var shortId = ""
      var title = ""
      var url = ""
      var createdAt = ""
      var commentCount: Int? = null
      var commentsUrl = ""
      var submitterName = ""
      var submitterAvatarUrl = ""
      var tags = emptyList<String>()
      var description = ""
      while (true) {
        when (val index = decodeElementIndex(descriptor)) {
          0 -> shortId = decodeStringElement(descriptor, 0)
          1 -> title = decodeStringElement(descriptor, 1)
          2 -> url = decodeStringElement(descriptor, 2)
          3 -> createdAt = decodeStringElement(descriptor, 3)
          4 -> commentCount = decodeNullableSerializableElement(descriptor, 4, Int.serializer())
          5 -> commentsUrl = decodeStringElement(descriptor, 5)
          6 -> submitterName = decodeStringElement(descriptor, 6)
          7 -> submitterAvatarUrl = decodeStringElement(descriptor, 7)
          8 -> tags = decodeSerializableElement(descriptor, 8, delegateSerializer)
          9 -> description = decodeStringElement(descriptor, 9)
          CompositeDecoder.DECODE_DONE -> break
          else -> error("Unexpected index: $index")
        }
      }
      SavedPost(
        shortId = shortId,
        title = title,
        url = url,
        createdAt = createdAt,
        commentCount = commentCount,
        commentsUrl = commentsUrl,
        submitterName = submitterName,
        submitterAvatarUrl = submitterAvatarUrl,
        tags = tags,
        description = description,
      )
    }
  }

  override fun serialize(encoder: Encoder, value: SavedPost) {
    encoder.encodeStructure(descriptor) {
      encodeStringElement(descriptor, 0, value.shortId)
      encodeStringElement(descriptor, 1, value.title)
      encodeStringElement(descriptor, 2, value.url)
      encodeStringElement(descriptor, 3, value.createdAt)
      encodeNullableSerializableElement(descriptor, 4, Int.serializer(), value.commentCount)
      encodeStringElement(descriptor, 5, value.commentsUrl)
      encodeStringElement(descriptor, 6, value.submitterName)
      encodeStringElement(descriptor, 7, value.submitterAvatarUrl)
      encodeSerializableElement(descriptor, 8, delegateSerializer, value.tags)
      encodeStringElement(descriptor, 9, value.description)
    }
  }
}
