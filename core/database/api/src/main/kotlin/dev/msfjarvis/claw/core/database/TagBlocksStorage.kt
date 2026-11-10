/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.core.database

import dev.msfjarvis.claw.model.TagBlock
import kotlinx.coroutines.flow.Flow

/** Persistence operations for saved tag blocks; expiration policy remains with the caller. */
interface TagBlocksStorage {
  val savedTags: Flow<Set<String>>
  val tagBlocks: Flow<List<TagBlock>>

  suspend fun getSavedTags(): Set<String>

  suspend fun getTagBlocks(): List<TagBlock>

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?)

  suspend fun replaceTagBlocks(blocks: List<TagBlock>)

  suspend fun removeTagBlock(tag: String)

  suspend fun removeExpiredTags()
}
