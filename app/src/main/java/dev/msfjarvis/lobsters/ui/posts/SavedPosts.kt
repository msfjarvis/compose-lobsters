package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.R
import dev.msfjarvis.lobsters.data.local.SavedPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.util.IconResource
import dev.msfjarvis.lobsters.util.asZonedDateTime
import dev.msfjarvis.lobsters.utils.Strings
import dev.msfjarvis.lobsters.utils.stringValue
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedPosts(
  posts: List<SavedPost>,
  sortReversed: Flow<Boolean>,
  modifier: Modifier = Modifier,
  saveAction: (SavedPost) -> Unit,
) {
  val listState = rememberLazyListState()
  val urlLauncher = LocalUrlLauncher.current
  val sortOrder by sortReversed.collectAsState(false)

  if (posts.isEmpty()) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      IconResource(
        R.drawable.ic_favorite_border_24px,
        tint = Color(0xFFD97373),
        modifier = Modifier.padding(16.dp),
        contentDescription = stringValue(Strings.AddToSavedPosts),
      )
      Text(stringValue(Strings.NoSavedPost))
    }
  } else {
    LazyColumn(
      state = listState,
      modifier = Modifier.then(modifier),
    ) {
      val grouped = posts.groupBy { it.createdAt.asZonedDateTime().month }
      grouped.forEach { (month, posts) ->
        stickyHeader {
          MonthHeader(month = month)
        }
        @Suppress("NAME_SHADOWING")
        val posts = if (sortOrder) posts.reversed() else posts
        items(posts) { item ->
          LobstersItem(
            post = item,
            isSaved = true,
            viewPost = { urlLauncher.launch(item.url.ifEmpty { item.commentsUrl }) },
            viewComments = { urlLauncher.launch(item.commentsUrl) },
            toggleSave = { saveAction.invoke(item) },
          )
        }
      }
    }
  }
}
