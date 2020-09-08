package dev.msfjarvis.todo.data.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.msfjarvis.todo.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

/**
 * Room [Dao] for [TodoItem]
 */
@Dao
abstract class TodoItemDao {

  @Query("SELECT * FROM todo_items")
  abstract fun getAllItems(): Flow<List<TodoItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insert(entity: TodoItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertAll(vararg entity: TodoItem)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun insertAll(entities: Collection<TodoItem>)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  abstract suspend fun update(entity: TodoItem)

  @Delete
  abstract suspend fun delete(entity: TodoItem): Int
}
