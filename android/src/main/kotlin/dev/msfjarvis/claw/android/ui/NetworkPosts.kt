package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.claw.android.ext.toDbModel
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.database.local.SavedPost
import dev.msfjarvis.claw.model.LobstersPost
import kotlinx.coroutines.launch

@Composable
fun NetworkPosts(
  items: LazyPagingItems<LobstersPost>,
  listState: LazyListState,
  launchUrl: (String) -> Unit,
  isSaved: suspend (SavedPost) -> Boolean,
  toggleSave: (SavedPost) -> Unit,
  viewComments: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  LazyColumn(
    state = listState,
    modifier = Modifier.then(modifier),
  ) {
    items(items) { item ->
      if (item != null) {
        val dbModel = item.toDbModel()
        var saved by remember(dbModel) { mutableStateOf(false) }
        coroutineScope.launch { saved = isSaved(dbModel) }
        LobstersCard(
          post = dbModel,
          isSaved = saved,
          viewPost = { launchUrl(item.url.ifEmpty { item.commentsUrl }) },
          viewComments = { viewComments(item.shortId) },
          toggleSave = { toggleSave(dbModel) },
          modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        )
      }
    }
  }
}
