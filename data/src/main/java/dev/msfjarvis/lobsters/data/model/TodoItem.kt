package dev.msfjarvis.lobsters.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(
  tableName = "todo_items",
)
data class TodoItem(
  @PrimaryKey val title: String,
  val time: LocalDateTime = LocalDateTime.now(ZoneId.of("GMT")),
)
