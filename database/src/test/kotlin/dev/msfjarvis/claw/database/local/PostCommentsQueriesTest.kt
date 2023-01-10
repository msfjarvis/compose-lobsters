/*
 * Copyright © 2023 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.CSVAdapter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.util.UUID

class PostCommentsQueriesTest : FunSpec() {
  private lateinit var postQueries: PostCommentsQueries
  init {
    beforeEach {
      val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
      LobstersDatabase.Schema.create(driver)
      val database =
        LobstersDatabase(
          driver,
          PostComments.Adapter(CSVAdapter()),
          SavedPost.Adapter(IntColumnAdapter, CSVAdapter()),
        )
      postQueries = database.postCommentsQueries
    }

    test("get non-existent post") {
      val ids = postQueries.getCommentIds(UUID.randomUUID().toString()).executeAsOneOrNull()
      ids shouldBe null
    }

    test("put and get post comments") {
      val postId = UUID.randomUUID().toString()
      val comments = PostComments(postId, List(10) { UUID.randomUUID().toString() })
      postQueries.rememberComments(comments)

      val ids = postQueries.getCommentIds(postId).executeAsOne().commentIds
      ids shouldHaveSize 10
    }
  }
}
