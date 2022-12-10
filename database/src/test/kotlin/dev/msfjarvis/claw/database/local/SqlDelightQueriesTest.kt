/*
 * Copyright © 2021-2022 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import dev.msfjarvis.claw.database.LobstersDatabase
import dev.msfjarvis.claw.database.model.TagsAdapter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class SqlDelightQueriesTest : FunSpec() {

  private lateinit var postQueries: SavedPostQueries

  init {
    beforeEach {
      val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
      LobstersDatabase.Schema.create(driver)
      val database =
        LobstersDatabase(
          driver,
          SavedPost.Adapter(IntColumnAdapter, TagsAdapter()),
        )
      postQueries = database.savedPostQueries
    }

    test("add and count posts") {
      val posts = createTestData(5)

      posts.forEach { postQueries.insertOrReplacePost(it) }

      val postCount = postQueries.selectCount().executeAsOne()

      postCount shouldBe 5
    }

    test("update post in database") {
      // Get 1 post
      val post = createTestData(1)[0]

      // Insert post into DB
      postQueries.insertOrReplacePost(post)

      // Create a new post and try replacing it
      val newPost = post.copy(submitterName = "Fake name")
      postQueries.insertOrReplacePost(newPost)

      // Check post count
      val postsCount = postQueries.selectCount().executeAsOne()
      postsCount shouldBe 1

      // Check if post is updated
      val postFromDb = postQueries.selectPost(post.shortId).executeAsOne()

      postFromDb.submitterName shouldBe "Fake name"
    }

    test("get post from db") {
      // Get 1 post
      val post = createTestData(1)[0]

      // Insert post into DB
      postQueries.insertOrReplacePost(post)

      val postFromDb = postQueries.selectAllPosts().executeAsOne()
      postFromDb.shortId shouldBe "test_id_1"
    }

    test("get multiple posts from db") {
      // Get 5 post
      val posts = createTestData(5)

      // Insert posts into DB
      posts.forEach { postQueries.insertOrReplacePost(it) }

      val postsFromDb = postQueries.selectAllPosts().executeAsList()

      // Check if all posts have correct shortId
      postsFromDb.forEachIndexed { index, post -> post.shortId shouldBe "test_id_${index.inc()}" }
    }

    test("delete post") {
      // Create 3 posts and insert them to DB
      val posts = createTestData(3)
      posts.forEach { postQueries.insertOrReplacePost(it) }

      // Delete 2nd post
      postQueries.deletePost("test_id_2")

      val postsFromDB = postQueries.selectAllPosts().executeAsList()

      // Check if size is 2, and only the correct post is deleted
      postsFromDB shouldHaveSize 2
      postsFromDB[0].shortId shouldBe "test_id_1"
      postsFromDB[1].shortId shouldBe "test_id_3"
    }

    test("delete all posts") {
      // Create 5 posts and insert them to DB
      val posts = createTestData(5)
      posts.forEach { postQueries.insertOrReplacePost(it) }

      // Delete all posts
      postQueries.deleteAllPosts()

      val dbPosts = postQueries.selectAllPosts().executeAsList()

      dbPosts.shouldBeEmpty()
    }
  }

  private fun createTestData(count: Int): ArrayList<SavedPost> {
    val posts = arrayListOf<SavedPost>()

    for (i in 1..count) {
      val post =
        SavedPost(
          shortId = "test_id_$i",
          createdAt = "0",
          title = "test",
          url = "test_url",
          commentCount = 0,
          commentsUrl = "test_comments_url",
          submitterName = "test_user_$i",
          submitterAvatarUrl = "test_avatar_url",
          tags = listOf(),
          description = "",
        )

      posts.add(post)
    }

    return posts
  }
}
