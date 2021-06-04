import androidx.compose.desktop.Window
import dev.msfjarvis.claw.common.posts.LobstersItem
import dev.msfjarvis.claw.common.posts.TEST_POST
import dev.msfjarvis.claw.common.theme.LobstersTheme

fun main() = Window {
  LobstersTheme(darkTheme = true) {
    LobstersItem(
      post = TEST_POST,
      isSaved = false,
      {},
      {},
      {},
    )
  }
}
