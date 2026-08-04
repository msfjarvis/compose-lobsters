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
import dev.zacsweers.metro.Inject
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream

@Inject
@OptIn(ExperimentalSerializationApi::class)
class DataTransferRepository(
  private val json: Json,
  private val savedPostsRepository: SavedPostsRepository,
  @param:IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
  private val serializer = ListSerializer(SavedPostSerializer)

  suspend fun importPosts(input: InputStream): Result<Unit> {
    return try {
      val posts: List<SavedPost> =
        withContext(ioDispatcher) { json.decodeFromStream(serializer, input) }
      savedPostsRepository.savePosts(posts)
      Result.success(Unit)
    } catch (t: Throwable) {
      Result.failure(t)
    }
  }

  suspend fun exportPostsAsJson(output: OutputStream) {
    val posts = savedPostsRepository.getSavedPosts()
    withContext(ioDispatcher) { json.encodeToStream(serializer, posts, output) }
  }
}
