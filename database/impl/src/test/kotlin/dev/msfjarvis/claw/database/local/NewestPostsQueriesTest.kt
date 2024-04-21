/*
 * Copyright © 2024 Harsh Shandilya.
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class NewestPostsQueriesTest {
  private lateinit var queries: NewestPostsQueries

  @BeforeEach
  fun setup() {
    queries = setupDatabase().newestPostsQueries
  }

  @Test
  @Disabled("The test is correct but Truth's comparison of lists is not")
  fun insertCachedPosts() {
    val testData = createTestData(20)
    queries.transaction { testData.forEach(queries::addPost) }
    assertThat(testData).containsAtLeastElementsIn(queries.getPage(1).executeAsList())
  }

  @Test
  fun clearCache() {
    queries.transaction { createTestData(5).forEach(queries::addPost) }
    assertThat(queries.getPage(1).executeAsList()).isNotEmpty()
    queries.deletePage(1)
    assertThat(queries.getPage(1).executeAsList()).isEmpty()
  }

  private fun createTestData(pages: Int): ArrayList<NewestPosts> {
    val posts = arrayListOf<NewestPosts>()

    for (page in 1..pages) {
      for (i in 1..25) {
        val post =
          NewestPosts(
            shortId = "test_id_$i",
            page = page,
            createdAt = "2020-09-21T11:56:04.000-05:00",
            title = "test",
            url = "test_url",
            commentCount = 0,
            description = "",
            commentsUrl = "test_comments_url",
            submitter = "test_user_$i",
            tags = listOf(),
            isRead = true,
            isSaved = true,
          )
        posts.add(post)
      }
    }

    return posts
  }
}
