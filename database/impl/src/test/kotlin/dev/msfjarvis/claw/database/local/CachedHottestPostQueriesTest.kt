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

class CachedHottestPostQueriesTest {
  private lateinit var postQueries: CachedHottestPostQueries

  @BeforeEach
  fun setup() {
    postQueries = setupDatabase().cachedHottestPostQueries
  }

  @Test
  fun `insert and select all posts`() {
    val posts = createTestData(5)

    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    assertThat(postsFromDb).hasSize(5)
  }

  @Test
  fun `insert or replace post updates existing post`() {
    val post = createTestData(1).first()

    postQueries.insertOrReplacePost(post)

    val newPost = post.copy(submitterName = "Updated name")
    postQueries.insertOrReplacePost(newPost)

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    assertThat(postsFromDb).hasSize(1)
    assertThat(postsFromDb.first().submitterName).isEqualTo("Updated name")
  }

  @Test
  fun `select recent posts with limit`() {
    val posts = createTestData(10)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val recentPosts = postQueries.selectRecentPosts(5).executeAsList()

    assertThat(recentPosts).hasSize(5)
  }

  @Test
  fun `select recent posts returns all when limit exceeds count`() {
    val posts = createTestData(3)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val recentPosts = postQueries.selectRecentPosts(10).executeAsList()

    assertThat(recentPosts).hasSize(3)
  }

  @Test
  fun `delete all posts`() {
    val posts = createTestData(5)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    postQueries.deleteAllPosts()

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    assertThat(postsFromDb).isEmpty()
  }

  private fun createTestData(count: Int): List<CachedHottestPost> {
    return (1..count).map { i ->
      CachedHottestPost(
        shortId = "test_id_$i",
        createdAt = "2024-01-${i.toString().padStart(2, '0')}T00:00:00+00:00",
        title = "test_post_$i",
        url = "test_url_$i",
        commentCount = i,
        commentsUrl = "test_comments_url_$i",
        submitterName = "test_user_$i",
        tags = listOf("tag1", "tag2"),
        description = "description_$i",
        userIsAuthor = i % 2 == 0,
      )
    }
  }
}
