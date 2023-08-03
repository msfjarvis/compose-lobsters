/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.google.common.truth.Truth.assertThat
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.CSVAdapter
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PostCommentsQueriesTest {
  private lateinit var postQueries: PostCommentsQueries

  @BeforeEach
  fun setup() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    val database =
      LobstersDatabase(
        driver,
        CachedNewestPost.Adapter(IntColumnAdapter, IntColumnAdapter, CSVAdapter),
        PostComments.Adapter(CSVAdapter),
        SavedPost.Adapter(IntColumnAdapter, CSVAdapter),
      )
    postQueries = database.postCommentsQueries
  }

  @Test
  fun `get non-existent post`() {
    val ids = postQueries.getCommentIds(UUID.randomUUID().toString()).executeAsOneOrNull()
    assertThat(ids).isNull()
  }

  @Test
  fun `put and get post comments`() {
    val postId = UUID.randomUUID().toString()
    val comments = PostComments(postId, List(10) { UUID.randomUUID().toString() })
    postQueries.rememberComments(comments)

    val ids = postQueries.getCommentIds(postId).executeAsOne().commentIds
    assertThat(ids).hasSize(10)
  }
}
