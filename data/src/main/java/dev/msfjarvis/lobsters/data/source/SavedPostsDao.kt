package dev.msfjarvis.lobsters.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.msfjarvis.lobsters.data.model.SavedLobstersEntity
import dev.msfjarvis.lobsters.model.LobstersPost
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SavedPostsDao {
  @Query("SELECT * FROM lobsters_saved_posts")
  abstract fun loadPosts(): Flow<List<LobstersPost>>

  @Transaction
  open suspend fun insertPosts(vararg posts: LobstersPost) {
    insertPosts(posts.map { SavedLobstersEntity(it) })
  }

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  protected abstract suspend fun insertPosts(posts: List<SavedLobstersEntity>)

  @Transaction
  open suspend fun deletePosts(vararg posts: LobstersPost) {
    deletePosts(posts.map { SavedLobstersEntity(it) })
  }

  @Delete
  protected abstract suspend fun deletePosts(posts: List<SavedLobstersEntity>)

  @Query("DELETE FROM lobsters_saved_posts")
  abstract suspend fun deleteAllPosts()

  @Query("DELETE FROM lobsters_saved_posts WHERE shortId LIKE :shortId")
  abstract suspend fun deletePostById(shortId: String)

  @Query("SELECT EXISTS(SELECT 1 FROM lobsters_saved_posts WHERE shortId LIKE :shortId)")
  abstract suspend fun isLiked(shortId: String): Boolean
}
