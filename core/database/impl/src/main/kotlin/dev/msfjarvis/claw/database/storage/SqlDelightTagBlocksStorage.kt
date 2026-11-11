/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.storage

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.msfjarvis.claw.core.coroutines.DatabaseReadDispatcher
import dev.msfjarvis.claw.core.coroutines.DatabaseWriteDispatcher
import dev.msfjarvis.claw.core.database.TagBlocksStorage
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightTagBlocksStorage(
  private val queries: TagBlocksQueries,
  @param:DatabaseReadDispatcher private val readDispatcher: CoroutineDispatcher,
  @param:DatabaseWriteDispatcher private val writeDispatcher: CoroutineDispatcher,
) : TagBlocksStorage {
  override val savedTags: Flow<Set<String>> =
    queries.selectActiveTags().asFlow().mapToList(readDispatcher).map { it.toSet() }

  override val tagBlocks: Flow<List<TagBlock>> =
    queries.selectAll().asFlow().mapToList(readDispatcher).map { blocks ->
      blocks.map { TagBlock(it.tag, it.expiration_millis) }
    }

  override suspend fun getSavedTags(): Set<String> =
    withContext(readDispatcher) { queries.selectActiveTags().executeAsList().toSet() }

  override suspend fun getTagBlocks(): List<TagBlock> =
    withContext(readDispatcher) {
      queries.selectAll().executeAsList().map { TagBlock(it.tag, it.expiration_millis) }
    }

  override suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    withContext(writeDispatcher) { queries.upsert(tag, expirationMillis) }
  }

  override suspend fun replaceTagBlocks(blocks: List<TagBlock>) {
    withContext(writeDispatcher) {
      queries.transaction {
        queries.deleteAll()
        blocks.forEach { block -> queries.insert(block.tag, block.expirationMillis) }
      }
    }
  }

  override suspend fun removeTagBlock(tag: String) {
    withContext(writeDispatcher) { queries.deleteByTag(tag) }
  }

  override suspend fun removeExpiredTags() {
    withContext(writeDispatcher) { queries.deleteExpired() }
  }
}
