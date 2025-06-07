/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.core.coroutines.IODispatcher
import dev.msfjarvis.claw.database.SavedPostSerializer
import dev.msfjarvis.claw.database.local.SavedPost
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

@OptIn(ExperimentalSerializationApi::class)
class DataTransferRepository
@Inject
constructor(
  private val json: Json,
  private val savedPostsRepository: SavedPostsRepository,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
  private val serializer = ListSerializer(SavedPostSerializer)

  suspend fun importPosts(input: InputStream) {
    val posts: List<SavedPost> =
      withContext(ioDispatcher) { json.decodeFromStream(serializer, input) }
    savedPostsRepository.savePosts(posts)
  }

  suspend fun exportPostsAsJson(output: OutputStream) {
    val posts = savedPostsRepository.savedPosts.first()
    withContext(ioDispatcher) { json.encodeToStream(serializer, posts, output) }
  }

  suspend fun exportPostsAsHTML(output: OutputStream) {
    fun computeTimestamp(post: SavedPost): Long {
      val temporal = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(post.createdAt)
      val instant = Instant.from(temporal)
      return instant.toEpochMilli()
    }

    val posts = savedPostsRepository.savedPosts.first()
    val header =
      """
      <!DOCTYPE NETSCAPE-Bookmark-file-1>
      <!-- This is an automatically generated file.
           It will be read and overwritten.
           DO NOT EDIT! -->
      <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
      <TITLE>Bookmarks</TITLE>
      <H1>Bookmarks</H1>
    """
        .trimIndent()
    val html = buildString {
      append(header)
      append("<DD><p>\n")
      for (post in posts) {
        append(
          """
          <DT><A HREF="${post.url.ifEmpty { post.commentsUrl }}" ADD_DATE="${computeTimestamp(post)}" PRIVATE="0" TAGS="${post.tags.joinToString(",")}">${post.title}</A>
          <DD>${post.title}

        """
            .trimIndent()
        )
      }
      append(
        """
        <DT><A HREF="https://example.com/" ADD_DATE="0" PRIVATE="0" TAGS="delete,me,pls">Padding post</A>
        <DD>Linkding ignores the last entry so this pads the difference for imports

      """
          .trimIndent()
      )
      append("</DD></p>\n")
    }
    withContext(ioDispatcher) {
      val writer = output.bufferedWriter()
      writer.write(html)
      writer.flush()
    }
  }
}
