/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.common.tags

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.local.TagBlocksQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TagBlockRepositoryTest {
  private lateinit var tagBlocksQueries: TagBlocksQueries
  private val testDispatcher = UnconfinedTestDispatcher()

  @BeforeEach
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    driver.execute(
      null,
      """
      CREATE TABLE IF NOT EXISTS TagBlocks (
        tag TEXT PRIMARY KEY NOT NULL,
        expiration_millis INTEGER
      )
      """
        .trimIndent(),
      0,
    )

    tagBlocksQueries = TagBlocksQueries(driver)
  }

  @Test
  fun `saveTagBlock inserts new tag block successfully`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    val expirationTime = System.currentTimeMillis() + 86400000
    repository.saveTagBlock("android", expirationTime)

    val tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("android")
  }

  @Test
  fun `saveTagBlock with null expiration creates permanent block`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    repository.saveTagBlock("kotlin", null)

    val tagBlocks = repository.getTagBlocks().first()
    assertThat(tagBlocks).hasSize(1)
    assertThat(tagBlocks[0].tag).isEqualTo("kotlin")
    assertThat(tagBlocks[0].isPermanent).isTrue()
  }

  @Test
  fun `saveTagBlock replaces existing tag block`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    val firstExpiration = System.currentTimeMillis() + 86400000
    repository.saveTagBlock("rust", firstExpiration)

    val secondExpiration = System.currentTimeMillis() + 172800000
    repository.saveTagBlock("rust", secondExpiration)

    val tagBlocks = repository.getTagBlocks().first()
    assertThat(tagBlocks).hasSize(1)
    assertThat(tagBlocks[0].expirationMillis).isEqualTo(secondExpiration)
  }

  @Test
  fun `removeTagBlock deletes tag successfully`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    repository.saveTagBlock("python", null)
    repository.saveTagBlock("javascript", null)

    var tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("python", "javascript")

    repository.removeTagBlock("python")

    tags = repository.getSavedTags().first()
    assertThat(tags).containsExactly("javascript")
  }

  @Test
  fun `getSavedTags filters out expired tags`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired-tag", pastTime)
    repository.saveTagBlock("active-tag", futureTime)
    repository.saveTagBlock("permanent-tag", null)

    val tags = repository.getSavedTags().first()

    assertThat(tags).containsExactly("active-tag", "permanent-tag")
    assertThat(tags).doesNotContain("expired-tag")
  }

  @Test
  fun `getTagBlocks returns all blocks including expired`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired", pastTime)
    repository.saveTagBlock("active", futureTime)
    repository.saveTagBlock("permanent", null)

    val tagBlocks = repository.getTagBlocks().first()

    assertThat(tagBlocks).hasSize(3)
    assertThat(tagBlocks.map { it.tag }).containsExactly("expired", "active", "permanent")
  }

  @Test
  fun `removeExpiredTags only removes expired blocks`() = runTest {
    val repository = TagBlockRepository(tagBlocksQueries, testDispatcher, testDispatcher)

    val pastTime = System.currentTimeMillis() - 1000
    val futureTime = System.currentTimeMillis() + 86400000

    repository.saveTagBlock("expired1", pastTime)
    repository.saveTagBlock("expired2", pastTime - 5000)
    repository.saveTagBlock("future", futureTime)
    repository.saveTagBlock("permanent", null)

    repository.removeExpiredTags()

    val remaining = repository.getTagBlocks().first()
    assertThat(remaining).hasSize(2)
    assertThat(remaining.map { it.tag }).containsExactly("future", "permanent")
  }
}
