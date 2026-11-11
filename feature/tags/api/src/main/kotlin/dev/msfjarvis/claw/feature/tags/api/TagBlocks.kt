/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.tags.api

import dev.msfjarvis.claw.model.TagBlock

/** Tag-block operations shared by filtering UI, feed rendering, and expiration cleanup. */
interface TagBlocks {
  suspend fun getSavedTagsSnapshot(): Set<String>

  suspend fun getTagBlocksSnapshot(): List<TagBlock>

  suspend fun saveTagBlock(tag: String, expirationMillis: Long?)

  suspend fun replaceTagBlocks(blocks: List<TagBlock>)

  suspend fun removeTagBlock(tag: String)

  suspend fun removeExpiredTags()
}
