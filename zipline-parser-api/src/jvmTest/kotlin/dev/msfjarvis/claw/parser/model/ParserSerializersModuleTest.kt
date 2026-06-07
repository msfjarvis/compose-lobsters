/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.parser.model

import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class ParserSerializersModuleTest {
  private val json = Json {
    serializersModule = ParserSerializersModule
    encodeDefaults = true
    ignoreUnknownKeys = true
    allowStructuredMapKeys = true
  }

  @Test
  fun moduleResolvesAllZiplineServiceBoundaryTypes() {
    json.serializerFromModule<List<LobstersPost>>()
    json.serializerFromModule<LobstersPostDetails>()
    json.serializerFromModule<User>()
    json.serializerFromModule<List<Tag>>()
    json.serializerFromModule<CSRFToken>()
    json.serializerFromModule<ReplyForm>()
  }

  @Test
  fun roundTripsParserBoundaryValues() {
    val post =
      LobstersPost(
        shortId = "abc123",
        createdAt = "2026-06-07T10:15:30Z",
        title = "Parser extraction works",
        url = "https://example.com/post",
        description = "A parser boundary post",
        commentCount = 42,
        commentsUrl = "https://lobste.rs/s/abc123/parser_extraction_works",
        submitter = "msfjarvis",
        userIsAuthor = true,
        tags = listOf("kotlin", "android"),
      )
    assertRoundTripListOfPosts(listOf(post), json.serializerFromModule<List<LobstersPost>>())

    val details =
      LobstersPostDetails(
        shortId = "abc123",
        createdAt = "2026-06-07T10:15:30Z",
        title = "Parser extraction works",
        url = "https://example.com/post",
        description = "A parser boundary post",
        commentCount = 1,
        commentsUrl = "https://lobste.rs/s/abc123/parser_extraction_works",
        submitter = "msfjarvis",
        tags = listOf("kotlin"),
        comments =
          listOf(
            Comment(
              shortId = "def456",
              comment = "Looks good",
              url = "https://lobste.rs/s/abc123/parser_extraction_works#c_def456",
              score = 3,
              timestamp = 1_812_345_678L,
              edited = true,
              parentComment = "parent1",
              user = "commenter",
              isUpvoted = true,
            )
          ),
        userIsAuthor = true,
      )
    assertRoundTripPostDetails(details, json.serializerFromModule<LobstersPostDetails>())

    assertRoundTripUser(
      User(
        username = "msfjarvis",
        about = "Maintainer",
        invitedBy = "inviter",
        avatarUrl = "https://lobste.rs/avatar.png",
        createdAt = "2020-01-01",
      ),
      json.serializerFromModule<User>(),
    )

    assertRoundTripListOfTags(
      listOf(
        Tag(
          tag = "kotlin",
          description = "Kotlin language",
          privileged = true,
          active = true,
          category = "programming",
          isMedia = false,
          hotnessMod = 1.5,
        )
      ),
      json.serializerFromModule<List<Tag>>(),
    )

    assertRoundTripCsrfToken(CSRFToken("csrf-token"), json.serializerFromModule<CSRFToken>())

    assertRoundTripReplyForm(
      ReplyForm(
        authenticityToken = "csrf-token",
        storyId = "123",
        method = "post",
        parentCommentShortId = "def456",
      ),
      json.serializerFromModule<ReplyForm>(),
    )
  }

  @Test
  fun deserializationUsesParserDtoDefaultsForMissingOptionalFields() {
    val serializer = json.serializerFromModule<LobstersPost>()

    val decoded =
      json.decodeFromString(
        serializer,
        """
        {
          "shortId": "abc123",
          "title": "Only required fields",
          "submitter": "msfjarvis"
        }
        """
          .trimIndent(),
      )

    assertEquals("abc123", decoded.shortId)
    assertEquals("Only required fields", decoded.title)
    assertEquals("msfjarvis", decoded.submitter)
    assertEquals("", decoded.createdAt)
    assertEquals("", decoded.url)
    assertEquals("", decoded.description)
    assertEquals(0, decoded.commentCount)
    assertEquals("", decoded.commentsUrl)
    assertEquals(false, decoded.userIsAuthor)
    assertTrue(decoded.tags.isEmpty())
  }

  private fun assertRoundTripListOfPosts(
    value: List<LobstersPost>,
    serializer: KSerializer<List<LobstersPost>>,
  ) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertEquals(value.size, decoded.size)
    value.zip(decoded).forEach { (expected, actual) -> assertPostEquals(expected, actual) }
  }

  private fun assertRoundTripPostDetails(
    value: LobstersPostDetails,
    serializer: KSerializer<LobstersPostDetails>,
  ) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertPostDetailsEquals(value, decoded)
  }

  private fun assertRoundTripUser(value: User, serializer: KSerializer<User>) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertUserEquals(value, decoded)
  }

  private fun assertRoundTripListOfTags(value: List<Tag>, serializer: KSerializer<List<Tag>>) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertEquals(value.size, decoded.size)
    value.zip(decoded).forEach { (expected, actual) -> assertTagEquals(expected, actual) }
  }

  private fun assertRoundTripCsrfToken(value: CSRFToken, serializer: KSerializer<CSRFToken>) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertEquals(value, decoded)
  }

  private fun assertRoundTripReplyForm(value: ReplyForm, serializer: KSerializer<ReplyForm>) {
    val decoded = json.decodeFromString(serializer, json.encodeToString(serializer, value))
    assertReplyFormEquals(value, decoded)
  }

  private fun assertPostEquals(expected: LobstersPost, actual: LobstersPost) {
    assertEquals(expected.shortId, actual.shortId)
    assertEquals(expected.createdAt, actual.createdAt)
    assertEquals(expected.title, actual.title)
    assertEquals(expected.url, actual.url)
    assertEquals(expected.description, actual.description)
    assertEquals(expected.commentCount, actual.commentCount)
    assertEquals(expected.commentsUrl, actual.commentsUrl)
    assertEquals(expected.submitter, actual.submitter)
    assertEquals(expected.userIsAuthor, actual.userIsAuthor)
    assertEquals(expected.tags, actual.tags)
  }

  private fun assertPostDetailsEquals(expected: LobstersPostDetails, actual: LobstersPostDetails) {
    assertEquals(expected.shortId, actual.shortId)
    assertEquals(expected.createdAt, actual.createdAt)
    assertEquals(expected.title, actual.title)
    assertEquals(expected.url, actual.url)
    assertEquals(expected.description, actual.description)
    assertEquals(expected.commentCount, actual.commentCount)
    assertEquals(expected.commentsUrl, actual.commentsUrl)
    assertEquals(expected.submitter, actual.submitter)
    assertEquals(expected.tags, actual.tags)
    assertEquals(expected.userIsAuthor, actual.userIsAuthor)
    assertEquals(expected.comments.size, actual.comments.size)
    expected.comments.zip(actual.comments).forEach { (exp, act) -> assertCommentEquals(exp, act) }
  }

  private fun assertCommentEquals(expected: Comment, actual: Comment) {
    assertEquals(expected.shortId, actual.shortId)
    assertEquals(expected.comment, actual.comment)
    assertEquals(expected.url, actual.url)
    assertEquals(expected.score, actual.score)
    assertEquals(expected.timestamp, actual.timestamp)
    assertEquals(expected.edited, actual.edited)
    assertEquals(expected.parentComment, actual.parentComment)
    assertEquals(expected.user, actual.user)
    assertEquals(expected.isUpvoted, actual.isUpvoted)
  }

  private fun assertUserEquals(expected: User, actual: User) {
    assertEquals(expected.username, actual.username)
    assertEquals(expected.about, actual.about)
    assertEquals(expected.invitedBy, actual.invitedBy)
    assertEquals(expected.avatarUrl, actual.avatarUrl)
    assertEquals(expected.createdAt, actual.createdAt)
  }

  private fun assertTagEquals(expected: Tag, actual: Tag) {
    assertEquals(expected.tag, actual.tag)
    assertEquals(expected.description, actual.description)
    assertEquals(expected.privileged, actual.privileged)
    assertEquals(expected.active, actual.active)
    assertEquals(expected.category, actual.category)
    assertEquals(expected.isMedia, actual.isMedia)
    assertEquals(expected.hotnessMod, actual.hotnessMod)
  }

  private fun assertReplyFormEquals(expected: ReplyForm, actual: ReplyForm) {
    assertEquals(expected.authenticityToken, actual.authenticityToken)
    assertEquals(expected.storyId, actual.storyId)
    assertEquals(expected.method, actual.method)
    assertEquals(expected.parentCommentShortId, actual.parentCommentShortId)
  }

  @OptIn(ExperimentalSerializationApi::class)
  private inline fun <reified T> Json.serializerFromModule(): KSerializer<T> {
    @Suppress("UNCHECKED_CAST")
    return serializersModule.serializer(typeOf<T>()) as KSerializer<T>
  }
}
