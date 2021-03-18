package dev.msfjarvis.lobsters.data.local

import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.msfjarvis.lobsters.database.LobstersDatabase
import dev.msfjarvis.lobsters.model.TagsAdapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before

@OptIn(ExperimentalStdlibApi::class)
class SqlDelightQueriesTest {

  private lateinit var postQueries: SavedPostQueries

  @Before
  fun setUp() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    LobstersDatabase.Schema.create(driver)
    val database = LobstersDatabase(
      driver,
      SavedPost.Adapter(TagsAdapter()),
    )
    postQueries = database.savedPostQueries
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
    val newPost = post.copy(submitterName = "Fake name")
    postQueries.insertOrReplacePost(newPost)

    // Check post count
    val postsCount = postQueries.selectCount().executeAsOne()
    assertEquals(1, postsCount)

    // Check if post is updated
    val postFromDb = postQueries.selectPost(post.shortId).executeAsOne()
    assertEquals("Fake name", postFromDb.submitterName)
  }

  @Test
  fun selectPost() = runBlocking {
    // Get 1 post
    val post = createTestData(1)[0]

    // Insert post into DB
    postQueries.insertOrReplacePost(post)

    val postFromDb = postQueries.selectAllPosts().executeAsOne()
    assertEquals("test_id_1", postFromDb.shortId)
  }

  @Test
  fun selectAllPosts() = runBlocking {
    // Get 5 post
    val posts = createTestData(5)

    // Insert posts into DB
    posts.forEach { postQueries.insertOrReplacePost(it) }

    val postsFromDb = postQueries.selectAllPosts().executeAsList()

    // Check if all posts have correct shortId
    for (i in 1..5) {
      assertEquals("test_id_$i", postsFromDb[i - 1].shortId)
    }
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
    assertEquals("test_id_1", postsFromDB[0].shortId)
    assertEquals("test_id_3", postsFromDB[1].shortId)
  }

  @Test
  fun deleteAllPost() = runBlocking {
    // Create 5 posts and insert them to DB
    val posts = createTestData(5)
    posts.forEach { postQueries.insertOrReplacePost(it) }

    // Delete all posts
    postQueries.deleteAllPosts()

    val postsCount = postQueries.selectCount().executeAsOne()

    // Check if db is empty
    assertEquals(0, postsCount)
  }


  private fun createTestData(count: Int): ArrayList<SavedPost> {
    val posts = arrayListOf<SavedPost>()

    for (i in 1..count) {
      val post = SavedPost(
        shortId = "test_id_$i",
        createdAt = "0",
        title = "test",
        url = "test_url",
        commentsUrl = "test_comments_url",
        submitterName = "test_user_$i",
        submitterAvatarUrl = "test_avatar_url",
        tags = listOf(),
      )

      posts.add(post)
    }

    return posts
  }
}
