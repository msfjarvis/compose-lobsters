/*
 * Copyright © Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

  @Test
  fun `select posts from last N days`() {
    val now = Instant.now()
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    fun dateOffset(daysToSubtract: Long): String {
      return formatter.format(
        now.minus(daysToSubtract, ChronoUnit.DAYS).atOffset(java.time.ZoneOffset.UTC)
      )
    }

    val recentPost = createPostWithDate(id = "recent_1", createdAt = dateOffset(5))
    val oldPost = createPostWithDate(id = "old_1", createdAt = dateOffset(40))
    val veryRecentPost = createPostWithDate(id = "recent_2", createdAt = dateOffset(1))

    postQueries.insertOrReplacePost(recentPost)
    postQueries.insertOrReplacePost(oldPost)
    postQueries.insertOrReplacePost(veryRecentPost)

    val postsFromLast30Days = postQueries.selectPostsFromLastNDays("30").executeAsList()

    assertThat(postsFromLast30Days).hasSize(2)
    assertThat(postsFromLast30Days.map { it.shortId }).containsExactly("recent_1", "recent_2")
    assertThat(postsFromLast30Days.map { it.shortId }).doesNotContain("old_1")
  }

  @Test
  fun `postExists returns true when post exists`() {
    val post = createTestData(1).first()
    postQueries.insertOrReplacePost(post)

    val exists = postQueries.postExists("test_id_1").executeAsOne()

    assertThat(exists).isTrue()
  }

  @Test
  fun `postExists returns false when post does not exist`() {
    val exists = postQueries.postExists("nonexistent_id").executeAsOne()

    assertThat(exists).isFalse()
  }

  @Test
  fun `selectRecentPosts returns correct number of posts`() {
    val posts = createTestData(10)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val recentPosts = postQueries.selectRecentPosts(5).executeAsList()

    assertThat(recentPosts).hasSize(5)
  }

  @Test
  fun `selectRecentPosts returns all posts when limit exceeds count`() {
    val posts = createTestData(3)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val recentPosts = postQueries.selectRecentPosts(10).executeAsList()

    assertThat(recentPosts).hasSize(3)
  }

  @Test
  fun `selectRecentPosts returns posts in descending date order`() {
    val now = Instant.now()
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    fun dateOffset(daysToSubtract: Long): String {
      return formatter.format(
        now.minus(daysToSubtract, ChronoUnit.DAYS).atOffset(java.time.ZoneOffset.UTC)
      )
    }

    val oldest = createPostWithDate(id = "oldest", createdAt = dateOffset(30))
    val middle = createPostWithDate(id = "middle", createdAt = dateOffset(15))
    val newest = createPostWithDate(id = "newest", createdAt = dateOffset(1))

    postQueries.insertOrReplacePost(oldest)
    postQueries.insertOrReplacePost(middle)
    postQueries.insertOrReplacePost(newest)

    val recentPosts = postQueries.selectRecentPosts(10).executeAsList()

    assertThat(recentPosts.map { it.shortId })
      .containsExactly("newest", "middle", "oldest")
      .inOrder()
  }

  @Test
  fun `selectAllPostsSortedByDate returns posts in descending date order`() {
    val now = Instant.now()
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    fun dateOffset(daysToSubtract: Long): String {
      return formatter.format(
        now.minus(daysToSubtract, ChronoUnit.DAYS).atOffset(java.time.ZoneOffset.UTC)
      )
    }

    val post1 = createPostWithDate(id = "post_1", createdAt = dateOffset(20))
    val post2 = createPostWithDate(id = "post_2", createdAt = dateOffset(5))
    val post3 = createPostWithDate(id = "post_3", createdAt = dateOffset(10))
    val post4 = createPostWithDate(id = "post_4", createdAt = dateOffset(1))

    postQueries.insertOrReplacePost(post1)
    postQueries.insertOrReplacePost(post2)
    postQueries.insertOrReplacePost(post3)
    postQueries.insertOrReplacePost(post4)

    val sortedPosts = postQueries.selectAllPostsSortedByDate().executeAsList()

    assertThat(sortedPosts.map { it.shortId })
      .containsExactly("post_4", "post_2", "post_3", "post_1")
      .inOrder()
  }

  private fun createTestData(count: Int): ArrayList<SavedPost> {
    val posts = arrayListOf<SavedPost>()

    for (i in 1..count) {
      val post =
        createPostWithDate(
          id = "test_id_$i",
          createdAt = "0",
          submitterName = "test_user_$i",
          userIsAuthor = i % 2 == 0,
        )

      posts.add(post)
    }

    return posts
  }

  private fun createPostWithDate(
    id: String,
    createdAt: String,
    submitterName: String = "test_user",
    userIsAuthor: Boolean = false,
  ): SavedPost {
    return SavedPost(
      shortId = id,
      createdAt = createdAt,
      title = "test_post",
      url = "test_url",
      commentCount = 0,
      commentsUrl = "test_comments_url",
      submitterName = submitterName,
      tags = listOf(),
      description = "",
      userIsAuthor = userIsAuthor,
    )
  }
}
