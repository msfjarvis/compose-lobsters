package dev.msfjarvis.lobsters.data.local

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.msfjarvis.lobsters.database.LobstersDatabase
import dev.msfjarvis.lobsters.model.Submitter
import dev.msfjarvis.lobsters.model.SubmitterAdapter
import dev.msfjarvis.lobsters.model.TagsAdapter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SqlDelightQueriesTest {

  private lateinit var postQueries: PostQueries

  @Before
  fun setUp() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    val database = LobstersDatabase(
      driver,
      LobstersPost.Adapter(SubmitterAdapter(), TagsAdapter())
    )
    postQueries = database.postQueries
  }

  @Test
  fun selectCount() = runBlocking {
    val posts = createTestData(5)

    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postCount = postQueries.selectCount().executeAsOne()
    assertEquals(5, postCount)
  }

  @Test
  fun insertIntoDatabase() = runBlocking {
    // Get 5 posts
    val posts = createTestData(5)

    // Insert posts into DB
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Check post count
    val postsCount = postQueries.selectCount().executeAsOne()
    assertEquals(5, postsCount)
  }

  @Test
  fun replaceFromDatabase() = runBlocking {
    // Get 1 post
    val post = createTestData(1)[0]

    // Insert post into DB
    postQueries.insertOrReplacePost(post)

    // Create a new post and try replacing it
    val newPost = post.copy(is_saved = true)
    postQueries.insertOrReplacePost(newPost)

    // Check post count
    val postsCount = postQueries.selectCount().executeAsOne()
    assertEquals(1, postsCount)

    // Check if post is updated
    val postFromDb = postQueries.selectPost(post.short_id).executeAsOne()
    assertEquals(true, postFromDb.is_saved)
  }

  @Test
  fun selectPost() = runBlocking {
    // Get 1 post
    val post = createTestData(1)[0]

    // Insert post into DB
    postQueries.insertOrReplacePost(post)

    val postFromDb = postQueries.selectAllPosts().executeAsOne()
    assertEquals("test_id_1", postFromDb.short_id)
  }

  @Test
  fun selectAllPosts() = runBlocking {
    // Get 5 post
    val posts = createTestData(5)

    // Insert posts into DB
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    // Check if all posts have correct short_id
    for (i in 1..5) {
      assertEquals("test_id_$i", postsFromDb[i - 1].short_id)
    }
  }


  @Test
  fun savePost() = runBlocking {
    // Insert post to DB
    val post = createTestData(1)[0]
    postQueries.insertOrReplacePost(post)

    // Update is_saved property of post
    postQueries.savePost(post.short_id)

    // Get the post and check if is_saved is true
    val postFromDB = postQueries.selectPost(post.short_id).executeAsOne()
    assertEquals(true, postFromDB.is_saved)
  }

  @Test
  fun removeSavedPost() = runBlocking {
    val post = createTestData(1)[0]
    postQueries.insertOrReplacePost(post)

    postQueries.savePost(post.short_id)

    // Get the post and check if is_saved is true
    val postFromDB = postQueries.selectPost(post.short_id).executeAsOne()
    assertEquals(true, postFromDB.is_saved)

    postQueries.removeSavedPost(post.short_id)

    // Get the post and check if is_saved is false
    val updatedPostFromDB = postQueries.selectPost(post.short_id).executeAsOne()
    assertEquals(false, updatedPostFromDB.is_saved)
  }

  @Test
  fun deletePost() = runBlocking {
    // Create 3 posts and insert them to DB
    val posts = createTestData(3)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Delete 2nd post
    postQueries.deletePost("test_id_2")

    val postsFromDB = postQueries.selectAllPosts().executeAsList()

    // Check if size is 2, and only the correct post is deleted
    assertEquals(2, postsFromDB.size)
    assertEquals("test_id_1", postsFromDB[0].short_id)
    assertEquals("test_id_3", postsFromDB[1].short_id)
  }

  @Test
  fun deleteAllPost() = runBlocking {
    // Create 3 posts and insert them to DB
    val posts = createTestData(5)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Delete 2nd post
    postQueries.deleteAllPosts()

    val postsCount = postQueries.selectCount().executeAsOne()

    // Check if size is 2, and only the correct post is deleted
    assertEquals(0, postsCount)
  }


  private fun createTestData(count: Int): ArrayList<LobstersPost> {
    val posts = arrayListOf<LobstersPost>()

    for (i in 1..count) {
      val submitter = Submitter(
        username = "test_user_$i",
        createdAt = "0",
        about = "test",
        avatarUrl = "test_avatar_url",
        invitedByUser = "test_user",
        isAdmin = false,
        isModerator = false
      )

      val post = LobstersPost(
        short_id = "test_id_$i",
        short_id_url = "test_id_url",
        created_at = "0",
        title = "test",
        url = "test_url",
        score = 0,
        flags = 0,
        comment_count = 0,
        description = "test",
        comments_url = "test_comments_url",
        submitter_user = submitter,
        tags = listOf(),
        is_saved = false
      )

      posts.add(post)
    }

    return posts
  }
}
