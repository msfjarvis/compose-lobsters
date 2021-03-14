@file:JvmName("Main")
package dev.msfjarvis.lobsters.ui

import androidx.compose.desktop.Window
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.msfjarvis.lobsters.data.local.SavedPost
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.msfjarvis.lobsters.data.ApiRepository
import dev.msfjarvis.lobsters.model.LobstersPost
import dev.msfjarvis.lobsters.ui.urllauncher.LocalUrlLauncher
import dev.msfjarvis.lobsters.ui.urllauncher.UrlLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

val repository = ApiRepository()

@OptIn(ExperimentalStdlibApi::class)
fun main() = Window(title = "Claw for lobste.rs") {
  val urlLauncher = UrlLauncher()
  val coroutineScope = rememberCoroutineScope()
  var items by remember { mutableStateOf(emptyList<SavedPost>()) }
  coroutineScope.launch {
    withContext(Dispatchers.IO) {
      items = repository.loadPosts(0).map(::toDbModel)
    }
  }
  LobstersTheme {
    Box(
      modifier = Modifier.fillMaxSize(),
    ) {
      val stateVertical = rememberScrollState(0)
      Box(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(stateVertical),
      ) {
        if (items.isEmpty()) {
          Text("Loading...")
        } else {
          CompositionLocalProvider(LocalUrlLauncher provides urlLauncher) {
            Column {
              items.forEach {
                LobstersItem(it)
              }
            }
          }
        }
      }
      VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(stateVertical),
      )
    }
  }
}

fun toDbModel(post: LobstersPost): SavedPost {
  return SavedPost(
    shortId = post.shortId,
    title = post.title,
    url = post.url,
    createdAt = post.createdAt,
    commentsUrl = post.commentsUrl,
    submitterName = post.submitterUser.username,
    submitterAvatarUrl = post.submitterUser.avatarUrl,
    tags = post.tags,
  )
}
