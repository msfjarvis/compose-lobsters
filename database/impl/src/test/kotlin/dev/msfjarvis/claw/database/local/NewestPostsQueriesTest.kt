package dev.msfjarvis.claw.database.local

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NewestPostsQueriesTest {
  private lateinit var queries: NewestPostsQueries

  @BeforeEach
  fun setup() {
    queries = setupDatabase().newestPostsQueries
  }

  @Test
  fun insertCachedPosts() {
    val testData = createTestData(20)
    queries.transaction {
      testData.forEach(queries::addCachedPost)
    }
    assertThat(queries.getCachedPosts(20, 0).executeAsList())
      .containsExactlyElementsIn(testData)
  }

  private fun createTestData(count: Long): ArrayList<NewestPosts> {
    val posts = arrayListOf<NewestPosts>()

    for (i in 1..count) {
      val post =
        NewestPosts(
          id = i,
          shortId = "test_id_$i",
          shortIdUrl = "https://lobste.rs/test_id_$1",
          createdAt = "2020-09-21T11:56:04.000-05:00",
          title = "test",
          url = "test_url",
          score = 10L,
          flags = 0L,
          commentCount = 0L,
          description = "",
          descriptionPlain = "",
          commentsUrl = "test_comments_url",
          submitterUsername = "test_user_$i",
          submitterCreatedAt = "2020-09-21T11:56:04.000-05:00",
          submitterIsAdmin = false,
          submitterAbout = null,
          submitterIsModerator = false,
          submitterKarma = i * 5,
          submitterAvatarUrl = "test_avatar_url",
          submitterInvitedByUser = null,
          submitterGithubUsername = null,
          userIsAuthor = true,
          tags = listOf(),
        )

      posts.add(post)
    }

    return posts
  }
}
