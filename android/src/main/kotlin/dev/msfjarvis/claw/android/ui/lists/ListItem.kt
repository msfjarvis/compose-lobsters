package dev.msfjarvis.claw.android.ui.lists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.database.local.SavedPost

@Composable
fun ListItem(
  item: SavedPost,
  isSaved: suspend (SavedPost) -> Boolean,
  postActions: PostActions,
  modifier: Modifier = Modifier,
) {
  val saved by produceState(false, item) { value = isSaved(item) }
  LobstersCard(
    post = item,
    isSaved = saved,
    postActions = postActions,
    modifier = modifier,
  )
}
