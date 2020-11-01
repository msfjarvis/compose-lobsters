package dev.msfjarvis.lobsters.data.model

import androidx.room.Embedded
import androidx.room.Entity
import dev.msfjarvis.lobsters.model.LobstersPost

@Entity(
  tableName = "lobsters_saved_posts",
  primaryKeys = ["shortId"],
)
data class SavedLobstersEntity(
  @Embedded
  val post: LobstersPost
)
