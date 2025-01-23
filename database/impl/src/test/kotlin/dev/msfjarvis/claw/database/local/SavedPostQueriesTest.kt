/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SavedPostQueriesTest {
  private lateinit var postQueries: SavedPostQueries

  @BeforeEach
  fun setup() {
    postQueries = setupDatabase().savedPostQueries
  }

  @Test
  fun `add and count posts`() {
    val posts = createTestData(5)

    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postCount = postQueries.selectCount().executeAsOne()

    assertThat(postCount).isEqualTo(5)
  }

  @Test
  fun `update post in database`() {
    // Get 1 post
    val post = createTestData(1).first()

    // Insert post into DB
    postQueries.insertOrReplacePost(post)

    // Create a new post and try replacing it
    val newPost = post.copy(submitterName = "Fake name")
    postQueries.insertOrReplacePost(newPost)

    // Check post count
    val postsCount = postQueries.selectCount().executeAsOne()
    assertThat(postsCount).isEqualTo(1)

    // Check if post is updated
    val postFromDb = postQueries.selectAllPosts().executeAsOne()
    assertThat(postFromDb.submitterName).isEqualTo("Fake name")
  }

  @Test
  fun `get post from db`() {
    // Get 1 post
    val post = createTestData(1).first()

    // Insert post into DB
    postQueries.insertOrReplacePost(post)

    val postFromDb = postQueries.selectAllPosts().executeAsOne()
    assertThat(postFromDb.shortId).isEqualTo("test_id_1")
  }

  @Test
  fun `get multiple posts from db`() {
    // Get 5 post
    val posts = createTestData(5)

    // Insert posts into DB
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    // Check if all posts have correct shortId
    postsFromDb.forEachIndexed { index, post ->
      assertThat(post.shortId).isEqualTo("test_id_${index.inc()}")
    }
  }

  @Test
  fun `delete post`() {
    // Create 3 posts and insert them to DB
    val posts = createTestData(3)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Delete 2nd post
    postQueries.deletePost("test_id_2")

    val postsFromDB = postQueries.selectAllPosts().executeAsList()

    // Check if size is 2, and only the correct post is deleted
    assertThat(postsFromDB).hasSize(2)
    assertThat(postsFromDB[0].shortId).isEqualTo("test_id_1")
    assertThat(postsFromDB[1].shortId).isEqualTo("test_id_3")
  }

  @Test
  fun `delete all posts`() {
    // Create 5 posts and insert them to DB
    val posts = createTestData(5)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Delete all posts
    postQueries.deleteAllPosts()

    val dbPosts = postQueries.selectAllPosts().executeAsList()

    assertThat(dbPosts).isEmpty()
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
          tags = listOf(),
          description = "",
        )

      posts.add(post)
    }

    return posts
  }
}
