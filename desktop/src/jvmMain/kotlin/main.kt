import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import org.pushingpixels.aurora.component.AuroraVerticalScrollbar
import org.pushingpixels.aurora.skin.ceruleanSkin
import org.pushingpixels.aurora.window.AuroraWindow

fun main() = application {
  val urlLauncher = UrlLauncher()
  val state =
    rememberWindowState(
      placement = WindowPlacement.Floating,
      position = WindowPosition.Aligned(Alignment.Center),
    )
  val itemsList = (0 until 10).toList()
  AuroraWindow(
    skin = ceruleanSkin(),
    title = "Claw",
    state = state,
    undecorated = true,
    onCloseRequest = ::exitApplication,
  ) {
    LobstersTheme(darkTheme = false) {
      Box(
        modifier = Modifier.fillMaxSize(),
      ) {
        val listState = rememberLazyListState()
        LazyColumn(
          state = listState,
        ) {
          itemsIndexed(itemsList) { _, _ ->
            LobstersCard(
              post = TEST_POST,
              isSaved = false,
              viewPost = { urlLauncher.launch(TEST_POST.url.ifEmpty { TEST_POST.commentsUrl }) },
              viewComments = { urlLauncher.launch(TEST_POST.commentsUrl) },
              toggleSave = {},
              modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            )
          }
        }
        AuroraVerticalScrollbar(
          adapter = rememberScrollbarAdapter(listState),
          modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        )
      }
    }
  }
}
