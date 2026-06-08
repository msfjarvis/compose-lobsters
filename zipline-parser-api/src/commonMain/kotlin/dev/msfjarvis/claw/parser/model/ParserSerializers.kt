/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

val ParserSerializersModule: SerializersModule = SerializersModule {
  contextual(LobstersPost::class, LobstersPostSerializer)
  contextual(LobstersPostDetails::class, LobstersPostDetailsSerializer)
  contextual(Comment::class, CommentSerializer)
  contextual(User::class, UserSerializer)
  contextual(Tag::class, TagSerializer)
  contextual(CSRFToken::class, CSRFTokenSerializer)
  contextual(ReplyForm::class, ReplyFormSerializer)
}

internal object LobstersPostSerializer : KSerializer<LobstersPost> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.LobstersPost", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: LobstersPost) {
    encoder.encodeString(
      PacketWriter()
        .string(value.shortId)
        .string(value.createdAt)
        .string(value.title)
        .string(value.url)
        .string(value.description)
        .int(value.commentCount)
        .string(value.commentsUrl)
        .string(value.submitter)
        .boolean(value.userIsAuthor)
        .stringList(value.tags)
        .build()
    )
  }

  override fun deserialize(decoder: Decoder): LobstersPost {
    val reader = PacketReader(decoder.decodeString())
    return LobstersPost(
      shortId = reader.string(),
      createdAt = reader.string(),
      title = reader.string(),
      url = reader.string(),
      description = reader.string(),
      commentCount = reader.int(),
      commentsUrl = reader.string(),
      submitter = reader.string(),
      userIsAuthor = reader.boolean(),
      tags = reader.stringList(),
    )
  }
}

internal object LobstersPostDetailsSerializer : KSerializer<LobstersPostDetails> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor(
      "dev.msfjarvis.claw.parser.model.LobstersPostDetails",
      PrimitiveKind.STRING,
    )

  override fun serialize(encoder: Encoder, value: LobstersPostDetails) {
    val writer =
      PacketWriter()
        .string(value.shortId)
        .string(value.createdAt)
        .string(value.title)
        .string(value.url)
        .string(value.description)
        .int(value.commentCount)
        .string(value.commentsUrl)
        .string(value.submitter)
        .stringList(value.tags)
        .int(value.comments.size)
    value.comments.forEach { writer.string(CommentSerializer.toPayload(it)) }
    encoder.encodeString(writer.boolean(value.userIsAuthor).build())
  }

  override fun deserialize(decoder: Decoder): LobstersPostDetails {
    val reader = PacketReader(decoder.decodeString())
    val shortId = reader.string()
    val createdAt = reader.string()
    val title = reader.string()
    val url = reader.string()
    val description = reader.string()
    val commentCount = reader.int()
    val commentsUrl = reader.string()
    val submitter = reader.string()
    val tags = reader.stringList()
    val comments =
      List(reader.int()) {
        val reader = PacketReader(reader.string())
        Comment(
          shortId = reader.string(),
          comment = reader.string(),
          url = reader.string(),
          score = reader.int(),
          timestamp = reader.long(),
          edited = reader.boolean(),
          parentComment = reader.nullableString(),
          user = reader.string(),
          isUpvoted = reader.boolean(),
        )
      }
    return LobstersPostDetails(
      shortId = shortId,
      createdAt = createdAt,
      title = title,
      url = url,
      description = description,
      commentCount = commentCount,
      commentsUrl = commentsUrl,
      submitter = submitter,
      tags = tags,
      comments = comments,
      userIsAuthor = reader.boolean(),
    )
  }
}

internal object CommentSerializer : KSerializer<Comment> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.Comment", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Comment) {
    encoder.encodeString(toPayload(value))
  }

  override fun deserialize(decoder: Decoder): Comment {
    val reader = PacketReader(decoder.decodeString())
    return Comment(
      shortId = reader.string(),
      comment = reader.string(),
      url = reader.string(),
      score = reader.int(),
      timestamp = reader.long(),
      edited = reader.boolean(),
      parentComment = reader.nullableString(),
      user = reader.string(),
      isUpvoted = reader.boolean(),
    )
  }

  fun toPayload(value: Comment): String =
    PacketWriter()
      .string(value.shortId)
      .string(value.comment)
      .string(value.url)
      .int(value.score)
      .long(value.timestamp)
      .boolean(value.edited)
      .nullableString(value.parentComment)
      .string(value.user)
      .boolean(value.isUpvoted)
      .build()
}

internal object UserSerializer : KSerializer<User> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.User", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: User) {
    encoder.encodeString(
      PacketWriter()
        .string(value.username)
        .string(value.about)
        .nullableString(value.invitedBy)
        .string(value.avatarUrl)
        .string(value.createdAt)
        .build()
    )
  }

  override fun deserialize(decoder: Decoder): User {
    val reader = PacketReader(decoder.decodeString())
    return User(
      username = reader.string(),
      about = reader.string(),
      invitedBy = reader.nullableString(),
      avatarUrl = reader.string(),
      createdAt = reader.string(),
    )
  }
}

internal object TagSerializer : KSerializer<Tag> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.Tag", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Tag) {
    encoder.encodeString(
      PacketWriter()
        .string(value.tag)
        .string(value.description)
        .boolean(value.privileged)
        .boolean(value.active)
        .string(value.category)
        .boolean(value.isMedia)
        .double(value.hotnessMod)
        .build()
    )
  }

  override fun deserialize(decoder: Decoder): Tag {
    val reader = PacketReader(decoder.decodeString())
    return Tag(
      tag = reader.string(),
      description = reader.string(),
      privileged = reader.boolean(),
      active = reader.boolean(),
      category = reader.string(),
      isMedia = reader.boolean(),
      hotnessMod = reader.double(),
    )
  }
}

internal object CSRFTokenSerializer : KSerializer<CSRFToken> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.CSRFToken", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: CSRFToken) {
    encoder.encodeString(value.value)
  }

  override fun deserialize(decoder: Decoder): CSRFToken = CSRFToken(decoder.decodeString())
}

internal object ReplyFormSerializer : KSerializer<ReplyForm> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.ReplyForm", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: ReplyForm) {
    encoder.encodeString(
      PacketWriter()
        .string(value.authenticityToken)
        .string(value.storyId)
        .string(value.method)
        .string(value.parentCommentShortId)
        .build()
    )
  }

  override fun deserialize(decoder: Decoder): ReplyForm {
    val reader = PacketReader(decoder.decodeString())
    return ReplyForm(
      authenticityToken = reader.string(),
      storyId = reader.string(),
      method = reader.string(),
      parentCommentShortId = reader.string(),
    )
  }
}

private class PacketWriter {
  private val parts = StringBuilder()

  fun string(value: String): PacketWriter {
    parts.append(value.length).append(':').append(value)
    return this
  }

  fun nullableString(value: String?): PacketWriter {
    parts.append(value?.length ?: -1).append(':')
    if (value != null) parts.append(value)
    return this
  }

  fun int(value: Int): PacketWriter = string(value.toString())

  fun long(value: Long): PacketWriter = string(value.toString())

  fun double(value: Double): PacketWriter = string(value.toString())

  fun boolean(value: Boolean): PacketWriter = string(if (value) "1" else "0")

  fun stringList(values: List<String>): PacketWriter {
    int(values.size)
    values.forEach(::string)
    return this
  }

  fun build(): String = parts.toString()
}

private class PacketReader(private val payload: String) {
  private var index = 0

  fun string(): String {
    val separatorIndex = payload.indexOf(':', index)
    if (separatorIndex == -1) throw SerializationException("Malformed payload")
    val length = payload.substring(index, separatorIndex).toInt()
    index = separatorIndex + 1
    if (length < 0) throw SerializationException("Expected non-null string")
    val endIndex = index + length
    if (endIndex > payload.length) throw SerializationException("Malformed payload")
    return payload.substring(index, endIndex).also { index = endIndex }
  }

  fun nullableString(): String? {
    val separatorIndex = payload.indexOf(':', index)
    if (separatorIndex == -1) throw SerializationException("Malformed payload")
    val length = payload.substring(index, separatorIndex).toInt()
    index = separatorIndex + 1
    if (length < 0) return null
    val endIndex = index + length
    if (endIndex > payload.length) throw SerializationException("Malformed payload")
    return payload.substring(index, endIndex).also { index = endIndex }
  }

  fun int(): Int = string().toInt()

  fun long(): Long = string().toLong()

  fun double(): Double = string().toDouble()

  fun boolean(): Boolean = string() == "1"

  fun stringList(): List<String> = List(int()) { string() }
}
