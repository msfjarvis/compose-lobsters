import androidx.compose.desktop.Window
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.msfjarvis.claw.common.posts.LobstersItem
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.LobstersTheme

fun main() = Window {
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
            LobstersItem(
              post = TEST_POST,
              isSaved = false,
              {},
              {},
              {},
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
