package dev.msfjarvis.lobsters.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.msfjarvis.lobsters.data.model.LobstersEntity
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostsDao {
  @Query("SELECT * FROM lobsters_posts")
  abstract fun loadPosts(): Flow<List<LobstersPost>>

  @Transaction
  open suspend fun insertPosts(vararg posts: LobstersPost) {
    insertPosts(posts.map { LobstersEntity(it) })
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  protected abstract suspend fun insertPosts(posts: List<LobstersEntity>)

  @Transaction
  open suspend fun deletePosts(vararg posts: LobstersPost) {
    deletePosts(posts.map { LobstersEntity(it) })
  }

  @Delete
  protected abstract suspend fun deletePosts(posts: List<LobstersEntity>)

  @Query("DELETE FROM lobsters_posts")
  abstract suspend fun deleteAllPosts()
}
