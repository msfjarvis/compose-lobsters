package dev.msfjarvis.lobsters.ui.comments

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.msfjarvis.lobsters.model.LobstersPostDetails

@Composable
private fun CommentsPageInternal(
  details: LobstersPostDetails,
) {
  LazyColumn { items(details.comments) { CommentEntry(it) } }
}

@Composable
fun CommentsPage(postId: String, getDetails: suspend (String) -> LobstersPostDetails) {
}
