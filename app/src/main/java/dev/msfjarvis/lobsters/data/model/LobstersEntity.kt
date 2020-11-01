package dev.msfjarvis.lobsters.data.model

import androidx.room.Embedded
import androidx.room.Entity
import dev.msfjarvis.lobsters.model.LobstersPost

@Entity(
  tableName = "lobsters_posts",
  primaryKeys = ["shortId"],
)
data class LobstersEntity(
  @Embedded
  val post: LobstersPost
)
