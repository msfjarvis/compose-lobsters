/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.android.viewmodel

import dev.msfjarvis.claw.android.injection.DatabaseDispatcher
import dev.msfjarvis.claw.android.injection.IODispatcher
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.SavedPostSerializer
import dev.msfjarvis.claw.database.local.SavedPost
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
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
  private val database: LobstersDatabase,
  private val json: Json,
  @DatabaseDispatcher private val dbDispatcher: CoroutineDispatcher,
  @IODispatcher private val ioDispatcher: CoroutineDispatcher,
) {
  private val serializer = ListSerializer(SavedPostSerializer)
  private val savedPostQueries = database.savedPostQueries

  suspend fun importPosts(input: InputStream) {
    val posts: List<SavedPost> =
      withContext(ioDispatcher) {
        json.decodeFromStream(
          serializer,
          input,
        )
      }
    withContext(dbDispatcher) {
      database.transaction { posts.forEach(savedPostQueries::insertOrReplacePost) }
    }
  }

  suspend fun exportPosts(output: OutputStream) {
    val posts = withContext(dbDispatcher) { savedPostQueries.selectAllPosts().executeAsList() }
    withContext(ioDispatcher) { json.encodeToStream(serializer, posts, output) }
  }
}
