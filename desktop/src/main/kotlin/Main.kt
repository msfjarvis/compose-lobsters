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
import dev.msfjarvis.lobsters.data.local.SavedPost

val TEST_POST = SavedPost(
  shortId = "zqyydb",
  title = "k2k20 hackathon report: Bob Beck on LibreSSL progress",
  url = "https://undeadly.org/cgi?action=article;sid=20200921105847",
  createdAt = "2020-09-21T07:11:14.000-05:00",
  commentsUrl = "https://lobste.rs/s/zqyydb/k2k20_hackathon_report_bob_beck_on",
  submitterName = "Vigdis",
  submitterAvatarUrl = "/avatars/Vigdis-100.png",
  tags = listOf("openbsd", "linux", "containers", "hack the planet", "no thanks"),
)

@OptIn(ExperimentalStdlibApi::class)
fun main() = Window(title = "Claw for lobste.rs") {
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
        Column {
          repeat(50) {
            LobstersItem(TEST_POST)
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
