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
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

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

  override fun deserialize(decoder: Decoder): LobstersPost =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toLobstersPost() },
      onPayload = { payload ->
        val reader = PacketReader(payload)
        LobstersPost(
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
      },
    )
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

  override fun deserialize(decoder: Decoder): LobstersPostDetails =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toLobstersPostDetails() },
      onPayload = { payload ->
        val reader = PacketReader(payload)
        val shortId = reader.string()
        val createdAt = reader.string()
        val title = reader.string()
        val url = reader.string()
        val description = reader.string()
        val commentCount = reader.int()
        val commentsUrl = reader.string()
        val submitter = reader.string()
        val tags = reader.stringList()
        val comments = List(reader.int()) { CommentSerializer.fromPayload(reader.string()) }
        LobstersPostDetails(
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
      },
    )
}

internal object CommentSerializer : KSerializer<Comment> {
  override val descriptor: SerialDescriptor =
    PrimitiveSerialDescriptor("dev.msfjarvis.claw.parser.model.Comment", PrimitiveKind.STRING)

  override fun serialize(encoder: Encoder, value: Comment) {
    encoder.encodeString(toPayload(value))
  }

  override fun deserialize(decoder: Decoder): Comment =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toComment() },
      onPayload = { payload -> fromPayload(payload) },
    )

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

  fun fromPayload(payload: String): Comment {
    val reader = PacketReader(payload)
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

  override fun deserialize(decoder: Decoder): User =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toUser() },
      onPayload = { payload ->
        val reader = PacketReader(payload)
        User(
          username = reader.string(),
          about = reader.string(),
          invitedBy = reader.nullableString(),
          avatarUrl = reader.string(),
          createdAt = reader.string(),
        )
      },
    )
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

  override fun deserialize(decoder: Decoder): Tag =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toTag() },
      onPayload = { payload ->
        val reader = PacketReader(payload)
        Tag(
          tag = reader.string(),
          description = reader.string(),
          privileged = reader.boolean(),
          active = reader.boolean(),
          category = reader.string(),
          isMedia = reader.boolean(),
          hotnessMod = reader.double(),
        )
      },
    )
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

  override fun deserialize(decoder: Decoder): ReplyForm =
    decoder.decodeJsonObjectOrPayload(
      onObject = { it.toReplyForm() },
      onPayload = { payload ->
        val reader = PacketReader(payload)
        ReplyForm(
          authenticityToken = reader.string(),
          storyId = reader.string(),
          method = reader.string(),
          parentCommentShortId = reader.string(),
        )
      },
    )
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

private inline fun <T> Decoder.decodeJsonObjectOrPayload(
  onObject: (JsonObject) -> T,
  onPayload: (String) -> T,
): T {
  val jsonDecoder = this as? JsonDecoder
  if (jsonDecoder != null) {
    return when (val element = jsonDecoder.decodeJsonElement()) {
      is JsonObject -> onObject(element)
      is JsonPrimitive -> onPayload(element.content)
      else -> throw SerializationException("Unsupported payload element: $element")
    }
  }
  return onPayload(decodeString())
}

private fun JsonObject.toLobstersPost(): LobstersPost =
  LobstersPost(
    shortId = requiredString("shortId"),
    createdAt = optionalString("createdAt"),
    title = requiredString("title"),
    url = optionalString("url"),
    description = optionalString("description"),
    commentCount = optionalInt("commentCount", 0),
    commentsUrl = optionalString("commentsUrl"),
    submitter = requiredString("submitter"),
    userIsAuthor = optionalBoolean("userIsAuthor", false),
    tags = optionalStringList("tags"),
  )

private fun JsonObject.toLobstersPostDetails(): LobstersPostDetails =
  LobstersPostDetails(
    shortId = requiredString("shortId"),
    createdAt = optionalString("createdAt"),
    title = requiredString("title"),
    url = optionalString("url"),
    description = optionalString("description"),
    commentCount = optionalInt("commentCount", 0),
    commentsUrl = optionalString("commentsUrl"),
    submitter = requiredString("submitter"),
    tags = optionalStringList("tags"),
    comments = this["comments"]?.jsonArray?.map { it.jsonObject.toComment() } ?: emptyList(),
    userIsAuthor = optionalBoolean("userIsAuthor", false),
  )

private fun JsonObject.toComment(): Comment =
  Comment(
    shortId = requiredString("shortId"),
    comment = requiredString("comment"),
    url = optionalString("url"),
    score = optionalInt("score", 1),
    timestamp = requiredLong("timestamp"),
    edited = optionalBoolean("edited", false),
    parentComment = this["parentComment"]?.jsonPrimitive?.contentOrNull,
    user = optionalString("user"),
    isUpvoted = optionalBoolean("isUpvoted", false),
  )

private fun JsonObject.toUser(): User =
  User(
    username = requiredString("username"),
    about = optionalString("about"),
    invitedBy = this["invitedBy"]?.jsonPrimitive?.contentOrNull,
    avatarUrl = optionalString("avatarUrl"),
    createdAt = optionalString("createdAt"),
  )

private fun JsonObject.toTag(): Tag =
  Tag(
    tag = requiredString("tag"),
    description = requiredString("description"),
    privileged = optionalBoolean("privileged", false),
    active = optionalBoolean("active", true),
    category = optionalString("category"),
    isMedia = optionalBoolean("isMedia", false),
    hotnessMod = optionalString("hotnessMod").toDoubleOrNull() ?: 0.0,
  )

private fun JsonObject.toReplyForm(): ReplyForm =
  ReplyForm(
    authenticityToken = requiredString("authenticityToken"),
    storyId = requiredString("storyId"),
    method = requiredString("method"),
    parentCommentShortId = requiredString("parentCommentShortId"),
  )

private fun JsonObject.requiredString(name: String): String =
  this[name]?.jsonPrimitive?.contentOrNull
    ?: throw SerializationException("Missing required string field '$name'")

private fun JsonObject.optionalString(name: String): String =
  this[name]?.jsonPrimitive?.contentOrNull ?: ""

private fun JsonObject.optionalInt(name: String, default: Int): Int =
  this[name]?.jsonPrimitive?.intOrNull ?: default

private fun JsonObject.requiredLong(name: String): Long =
  this[name]?.jsonPrimitive?.longOrNull
    ?: throw SerializationException("Missing required long field '$name'")

private fun JsonObject.optionalBoolean(name: String, default: Boolean): Boolean =
  when (this[name]?.jsonPrimitive?.contentOrNull) {
    null -> default
    "true",
    "1" -> true
    else -> false
  }

private fun JsonObject.optionalStringList(name: String): List<String> =
  this[name]?.jsonArray?.map { it.jsonPrimitive.contentOrNull ?: "" } ?: emptyList()
