package dev.msfjarvis.lobsters.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.matrix.roomigrant.GenerateRoomMigrations
import dev.msfjarvis.lobsters.data.model.LobstersEntity
import dev.msfjarvis.lobsters.data.model.SavedLobstersEntity

@Database(
  entities = [
    LobstersEntity::class,
    SavedLobstersEntity::class
  ],
  version = 2,
  exportSchema = true,
)
@TypeConverters(
  LobstersApiTypeConverters::class,
)
@GenerateRoomMigrations
abstract class PostsDatabase : RoomDatabase() {
  abstract fun postsDao(): PostsDao
  abstract fun savedPostsDao(): SavedPostsDao
}
