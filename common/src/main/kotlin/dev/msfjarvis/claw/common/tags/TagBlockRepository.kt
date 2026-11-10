/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import dev.msfjarvis.claw.core.database.TagBlocksStorage
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class TagBlockRepository(private val storage: TagBlocksStorage) {
  fun getSavedTags(): Flow<Set<String>> = storage.savedTags

  fun getTagBlocks(): Flow<List<TagBlock>> = storage.tagBlocks

  suspend fun getSavedTagsSnapshot(): Set<String> = storage.getSavedTags()

  suspend fun getTagBlocksSnapshot(): List<TagBlock> = storage.getTagBlocks()

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    storage.saveTagBlock(tag, expirationMillis)
  }

  suspend fun replaceTagBlocks(blocks: List<TagBlock>) {
    storage.replaceTagBlocks(blocks)
  }

  suspend fun removeTagBlock(tag: String) {
    storage.removeTagBlock(tag)
  }

  suspend fun removeExpiredTags() {
    storage.removeExpiredTags()
  }
}
