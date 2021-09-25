package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.claw.android.ext.toDbModel
import dev.msfjarvis.claw.api.model.LobstersPost
import dev.msfjarvis.claw.common.posts.LobstersCard

@Composable
fun NetworkPosts(
  items: LazyPagingItems<LobstersPost>,
  launchUrl: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = Modifier.then(modifier),
  ) {
    items(items) { item ->
      if (item != null) {
        LobstersCard(
          post = item.toDbModel(),
          isSaved = false,
          viewPost = { launchUrl(item.url.ifEmpty { item.commentsUrl }) },
          viewComments = { launchUrl(item.commentsUrl) },
          toggleSave = {},
          modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        )
      }
    }
  }
}
