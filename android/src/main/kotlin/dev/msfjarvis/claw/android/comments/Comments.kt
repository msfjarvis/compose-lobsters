package dev.msfjarvis.claw.android.comments

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import dev.msfjarvis.claw.model.LobstersPostDetails

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
) {
  LazyColumn { items(details.comments) { CommentEntry(it) } }
}

@Composable
fun CommentsPage(
  postId: String,
  getDetails: suspend (String) -> LobstersPostDetails,
) {}
