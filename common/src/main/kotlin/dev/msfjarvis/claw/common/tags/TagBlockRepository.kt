/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import dev.msfjarvis.claw.core.database.TagBlocksStorage
import dev.msfjarvis.claw.feature.tags.api.TagBlocks
import dev.msfjarvis.claw.model.TagBlock
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow

@Inject
@ContributesBinding(AppScope::class, binding = binding<TagBlocks>())
class TagBlockRepository(private val storage: TagBlocksStorage) : TagBlocks {
  fun getSavedTags(): Flow<Set<String>> = storage.savedTags

  fun getTagBlocks(): Flow<List<TagBlock>> = storage.tagBlocks

  override suspend fun getSavedTagsSnapshot(): Set<String> = storage.getSavedTags()

  override suspend fun getTagBlocksSnapshot(): List<TagBlock> = storage.getTagBlocks()

  override suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
    storage.saveTagBlock(tag, expirationMillis)
  }

  override suspend fun replaceTagBlocks(blocks: List<TagBlock>) {
    storage.replaceTagBlocks(blocks)
  }

  override suspend fun removeTagBlock(tag: String) {
    storage.removeTagBlock(tag)
  }

  override suspend fun removeExpiredTags() {
    storage.removeExpiredTags()
  }
}
