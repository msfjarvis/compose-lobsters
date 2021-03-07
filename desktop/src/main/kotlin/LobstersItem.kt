import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.msfjarvis.lobsters.data.local.SavedPost
import java.awt.Desktop
import java.net.URI

@Composable
fun LobstersItem(
  post: SavedPost,
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .requiredHeight(48.dp)
      .clickable {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop()
            .isSupported(Desktop.Action.BROWSE)
        ) {
          Desktop.getDesktop().browse(URI(post.url))
        }
      }
  ) {
    Text(post.title)
  }
}
