/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.feature.tags.impl

import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.core.database.TagBlocksStorage
import dev.msfjarvis.claw.model.TagBlock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class TagBlockRepositoryTest {
  @Test
  fun `repository exposes saved tags and tag blocks from storage`() = runTest {
    val storage = FakeTagBlocksStorage()
    storage.replaceTagBlocks(listOf(TagBlock("android", null), TagBlock("kotlin", Long.MAX_VALUE)))
    val repository = TagBlockRepository(storage)

    assertThat(repository.getSavedTags().first()).containsExactly("android", "kotlin")
    assertThat(repository.getTagBlocks().first()).containsExactlyElementsIn(storage.blocks.value)
  }

  @Test
  fun `repository forwards tag updates to storage`() = runTest {
    val storage = FakeTagBlocksStorage()
    val repository = TagBlockRepository(storage)

    repository.saveTagBlock("android", null)

    val block = repository.getTagBlocksSnapshot().single()
    assertThat(block.tag).isEqualTo("android")
    assertThat(block.isPermanent).isTrue()
  }

  private class FakeTagBlocksStorage : TagBlocksStorage {
    val blocks = MutableStateFlow<List<TagBlock>>(emptyList())
    override val savedTags: Flow<Set<String>>
      get() = blocks.map { values ->
        values.filterNot(TagBlock::isExpired).map(TagBlock::tag).toSet()
      }

    override val tagBlocks: Flow<List<TagBlock>> = blocks

    override suspend fun getSavedTags() =
      blocks.value.filterNot(TagBlock::isExpired).map(TagBlock::tag).toSet()

    override suspend fun getTagBlocks() = blocks.value

    override suspend fun saveTagBlock(tag: String, expirationMillis: Long?) {
      blocks.value = blocks.value.filterNot { it.tag == tag } + TagBlock(tag, expirationMillis)
    }

    override suspend fun replaceTagBlocks(blocks: List<TagBlock>) {
      this.blocks.value = blocks
    }

    override suspend fun removeTagBlock(tag: String) {
      blocks.value = blocks.value.filterNot { it.tag == tag }
    }

    override suspend fun removeExpiredTags() {
      blocks.value = blocks.value.filterNot(TagBlock::isExpired)
    }
  }
}
