/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class ParserSerializersTest {
  @Test
  fun `lobsters post serializer encoder`() {
    val post =
      LobstersPost(
        shortId = "abc123",
        createdAt = "2026-06-11T10:00:00Z",
        title = "Compose Lobsters",
        url = "https://example.com/post",
        description = "A test post",
        commentCount = 42,
        commentsUrl = "https://example.com/post/comments",
        submitter = "msfjarvis",
        userIsAuthor = true,
        tags = listOf("compose", "kotlin"),
      )

    assertEquals(
      "6:abc12320:2026-06-11T10:00:00Z16:Compose Lobsters24:https://example.com/post11:A test post2:4233:https://example.com/post/comments9:msfjarvis1:11:27:compose6:kotlin",
      encode(LobstersPostSerializer, post),
    )
  }

  @Test
  fun `lobsters post serializer decoder`() {
    val decoded =
      decode(
        LobstersPostSerializer,
        "6:abc12320:2026-06-11T10:00:00Z16:Compose Lobsters24:https://example.com/post11:A test post2:4233:https://example.com/post/comments9:msfjarvis1:11:27:compose6:kotlin",
      )

    assertEquals("abc123", decoded.shortId)
    assertEquals("2026-06-11T10:00:00Z", decoded.createdAt)
    assertEquals("Compose Lobsters", decoded.title)
    assertEquals("https://example.com/post", decoded.url)
    assertEquals("A test post", decoded.description)
    assertEquals(42, decoded.commentCount)
    assertEquals("https://example.com/post/comments", decoded.commentsUrl)
    assertEquals("msfjarvis", decoded.submitter)
    assertTrue(decoded.userIsAuthor)
    assertEquals(listOf("compose", "kotlin"), decoded.tags)
  }

  @Test
  fun `lobsters post details encode with nested comments`() {
    val details =
      LobstersPostDetails(
        shortId = "post-1",
        createdAt = "2026-06-11T10:00:00Z",
        title = "Deep dive",
        url = "https://example.com/story",
        description = "Lots of details",
        commentCount = 2,
        commentsUrl = "https://example.com/story/comments",
        submitter = "submitter",
        tags = listOf("zipline", "serialization"),
        comments =
          listOf(
            Comment(
              shortId = "c1",
              comment = "First",
              url = "https://example.com/c1",
              score = 1,
              timestamp = 100L,
              edited = false,
              parentComment = null,
              user = "alice",
              isUpvoted = true,
            ),
            Comment(
              shortId = "c2",
              comment = "Second",
              url = "https://example.com/c2",
              score = 2,
              timestamp = 200L,
              edited = true,
              parentComment = "c1",
              user = "bob",
              isUpvoted = false,
            ),
          ),
        userIsAuthor = true,
      )

    assertEquals(
      "6:post-120:2026-06-11T10:00:00Z9:Deep dive25:https://example.com/story15:Lots of details1:234:https://example.com/story/comments9:submitter1:27:zipline13:serialization1:260:2:c15:First22:https://example.com/c11:13:1001:0-1:5:alice1:160:2:c26:Second22:https://example.com/c21:23:2001:12:c13:bob1:01:1",
      encode(LobstersPostDetailsSerializer, details),
    )
  }

  @Test
  fun `lobsters post details decoder`() {
    val decoded =
      decode(
        LobstersPostDetailsSerializer,
        "6:post-120:2026-06-11T10:00:00Z9:Deep dive25:https://example.com/story15:Lots of details1:234:https://example.com/story/comments9:submitter1:27:zipline13:serialization1:260:2:c15:First22:https://example.com/c11:13:1001:0-1:5:alice1:160:2:c26:Second22:https://example.com/c21:23:2001:12:c13:bob1:01:1",
      )

    assertEquals("post-1", decoded.shortId)
    assertEquals(2, decoded.comments.size)
    assertNull(decoded.comments[0].parentComment)
    assertTrue(decoded.comments[0].isUpvoted)
    assertEquals("c1", decoded.comments[1].parentComment)
    assertTrue(decoded.comments[1].edited)
    assertFalse(decoded.comments[1].isUpvoted)
    assertTrue(decoded.userIsAuthor)
  }

  @Test
  fun `user serializer encoder`() {
    val user =
      User(
        username = "msfjarvis",
        about = "About",
        invitedBy = null,
        avatarUrl = "https://example.com/avatar.png",
        createdAt = "2026-06-11",
      )

    assertEquals(
      "9:msfjarvis5:About-1:30:https://example.com/avatar.png10:2026-06-11",
      encode(UserSerializer, user),
    )
  }

  @Test
  fun `user serializer decoder`() {
    val decoded =
      decode(
        UserSerializer,
        "9:msfjarvis5:About-1:30:https://example.com/avatar.png10:2026-06-11",
      )

    assertEquals("msfjarvis", decoded.username)
    assertEquals("About", decoded.about)
    assertNull(decoded.invitedBy)
    assertEquals("https://example.com/avatar.png", decoded.avatarUrl)
    assertEquals("2026-06-11", decoded.createdAt)
  }

  @Test
  fun `tag serializer encoder`() {
    val tag =
      Tag(
        tag = "show",
        description = "Show HN style media post",
        privileged = true,
        active = false,
        category = "media",
        isMedia = true,
        hotnessMod = 1.5,
      )

    assertEquals(
      "4:show24:Show HN style media post1:11:05:media1:13:1.5",
      encode(TagSerializer, tag),
    )
  }

  @Test
  fun `tag serializer decoder`() {
    val decoded = decode(TagSerializer, "4:show24:Show HN style media post1:11:05:media1:13:1.5")

    assertEquals("show", decoded.tag)
    assertEquals("Show HN style media post", decoded.description)
    assertTrue(decoded.privileged)
    assertFalse(decoded.active)
    assertEquals("media", decoded.category)
    assertTrue(decoded.isMedia)
    assertEquals(1.5, decoded.hotnessMod)
  }

  @Test
  fun `csrf token serializer encodes raw token value`() {
    assertEquals("secret-token", encode(CSRFTokenSerializer, CSRFToken("secret-token")))
  }

  @Test
  fun `csrf token serializer decodes hand rolled payload`() {
    assertEquals("secret-token", decode(CSRFTokenSerializer, "secret-token").value)
  }

  @Test
  fun `reply form serializer encodes expected payload`() {
    val replyForm =
      ReplyForm(
        authenticityToken = "token",
        storyId = "story",
        method = "post",
        parentCommentShortId = "c123",
      )

    assertEquals("5:token5:story4:post4:c123", encode(ReplyFormSerializer, replyForm))
  }

  @Test
  fun `reply form serializer decodes hand rolled payload`() {
    val decoded = decode(ReplyFormSerializer, "5:token5:story4:post4:c123")

    assertEquals("token", decoded.authenticityToken)
    assertEquals("story", decoded.storyId)
    assertEquals("post", decoded.method)
    assertEquals("c123", decoded.parentCommentShortId)
  }

  @Test
  fun `filters page serializer encodes tag payloads and sorted blocked tags`() {
    val filtersPage =
      FiltersPage(
        authenticityToken = "token",
        tags =
          listOf(
            Tag(tag = "android", description = "Android posts"),
            Tag(tag = "compose", description = "Compose posts", active = false),
          ),
        blockedTags = setOf("compose", "android"),
      )

    assertEquals(
      "5:token1:241:7:android13:Android posts1:01:10:1:03:0.041:7:compose13:Compose posts1:01:00:1:03:0.01:27:android7:compose",
      encode(FiltersPageSerializer, filtersPage),
    )
  }

  @Test
  fun `filters page serializer decodes hand rolled payload`() {
    val decoded =
      decode(
        FiltersPageSerializer,
        "5:token1:241:7:android13:Android posts1:01:10:1:03:0.041:7:compose13:Compose posts1:01:00:1:03:0.01:27:android7:compose",
      )

    assertEquals("token", decoded.authenticityToken)
    assertEquals(2, decoded.tags.size)
    assertEquals("android", decoded.tags[0].tag)
    assertFalse(decoded.tags[1].active)
    assertEquals(setOf("android", "compose"), decoded.blockedTags)
  }

  private fun <T> encode(serializer: KSerializer<T>, value: T): String {
    return StringEncoder().also { serializer.serialize(it, value) }.payload
  }

  private fun <T> decode(serializer: KSerializer<T>, payload: String): T {
    return serializer.deserialize(StringDecoder(payload))
  }
}

@OptIn(ExperimentalSerializationApi::class)
private class StringEncoder : Encoder {
  var payload: String = ""
  override val serializersModule: SerializersModule = EmptySerializersModule()

  override fun encodeString(value: String) {
    payload = value
  }

  override fun beginCollection(
    descriptor: SerialDescriptor,
    collectionSize: Int,
  ): CompositeEncoder = unsupported()

  override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = unsupported()

  override fun encodeBoolean(value: Boolean) = unsupported()

  override fun encodeByte(value: Byte) = unsupported()

  override fun encodeChar(value: Char) = unsupported()

  override fun encodeDouble(value: Double) = unsupported()

  override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = unsupported()

  override fun encodeFloat(value: Float) = unsupported()

  override fun encodeInline(descriptor: SerialDescriptor): Encoder = unsupported()

  override fun encodeInt(value: Int) = unsupported()

  override fun encodeLong(value: Long) = unsupported()

  override fun encodeNotNullMark() = unsupported()

  override fun encodeNull() = unsupported()

  override fun encodeShort(value: Short) = unsupported()

  private fun unsupported(): Nothing = error("Unexpected encoder call")
}

@OptIn(ExperimentalSerializationApi::class)
private class StringDecoder(private val payload: String) : Decoder {
  override val serializersModule: SerializersModule = EmptySerializersModule()

  override fun decodeString(): String = payload

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = unsupported()

  override fun decodeBoolean(): Boolean = unsupported()

  override fun decodeByte(): Byte = unsupported()

  override fun decodeChar(): Char = unsupported()

  override fun decodeDouble(): Double = unsupported()

  override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = unsupported()

  override fun decodeFloat(): Float = unsupported()

  override fun decodeInline(descriptor: SerialDescriptor): Decoder = unsupported()

  override fun decodeInt(): Int = unsupported()

  override fun decodeLong(): Long = unsupported()

  override fun decodeNotNullMark(): Boolean = unsupported()

  override fun decodeNull(): Nothing = unsupported()

  override fun decodeShort(): Short = unsupported()

  private fun unsupported(): Nothing = error("Unexpected decoder call")
}
