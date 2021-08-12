import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher

fun main() = singleWindowApplication {
  val urlLauncher = UrlLauncher()
  LobstersTheme(darkTheme = false) {
    Box(
      modifier = Modifier.fillMaxSize(),
    ) {
      val stateVertical = rememberScrollState(0)
      Box(
        modifier = Modifier.fillMaxSize().verticalScroll(stateVertical),
      ) {
        Column {
          repeat(10) {
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
      }
      VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(stateVertical),
      )
    }
  }
}
