package dev.msfjarvis.lobsters.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostsDao {
  @Query("SELECT * FROM lobsters_posts")
  abstract fun loadPosts(): Flow<List<LobstersPost>>

  @Insert
  abstract suspend fun insertPosts(vararg posts: LobstersPost)

  @Delete
  abstract suspend fun deletePosts(vararg posts: LobstersPost)

  @Query("DELETE FROM lobsters_posts")
  abstract suspend fun deleteAllPosts()
}
