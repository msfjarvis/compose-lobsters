/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Inject
class TagBlockRepository(
  private val tagBlocksQueries: TagBlocksQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) {
  fun getSavedTags(): Flow<Set<String>> {
    val now = System.currentTimeMillis()
    return tagBlocksQueries.selectActiveTags(now).asFlow().mapToList(readDispatcher).map {
      it.toSet()
    }
  }

  fun getTagBlocks(): Flow<List<TagBlock>> {
    return tagBlocksQueries.selectAll().asFlow().mapToList(readDispatcher).map { blocks ->
      blocks.map { TagBlock(it.tag, it.expiration_millis) }
    }
  }

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    withContext(writeDispatcher) { tagBlocksQueries.insertOrReplace(tag, expirationMillis) }
  }

  suspend fun removeTagBlock(tag: String) {
    withContext(writeDispatcher) { tagBlocksQueries.deleteByTag(tag) }
  }

  suspend fun removeExpiredTags() {
    val now = System.currentTimeMillis()
    withContext(writeDispatcher) { tagBlocksQueries.deleteExpired(now) }
  }
}
