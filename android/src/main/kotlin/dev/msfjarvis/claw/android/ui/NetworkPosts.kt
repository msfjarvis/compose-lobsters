package dev.msfjarvis.claw.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import dev.msfjarvis.claw.android.ext.toDbModel
import dev.msfjarvis.claw.api.model.LobstersPost
import dev.msfjarvis.claw.common.posts.LobstersItem
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

@Composable
fun NetworkPosts(
  items: LazyPagingItems<LobstersPost>,
  urlLauncher: UrlLauncher,
) {
  LazyColumn {
    items(items) { item ->
      if (item != null) {
        LobstersItem(
          post = item.toDbModel(),
          isSaved = false,
          viewPost = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
          viewComments = { urlLauncher.launch(item.commentsUrl) },
          toggleSave = {},
        )
      }
    }
  }
}
