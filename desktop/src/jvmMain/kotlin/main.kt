import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import dev.msfjarvis.claw.api.LobstersApi
import dev.msfjarvis.claw.common.paging.Paging
import dev.msfjarvis.claw.common.posts.LobstersCard
import dev.msfjarvis.claw.common.posts.PostActions
import dev.msfjarvis.claw.common.posts.toDbModel
import dev.msfjarvis.claw.common.theme.DarkThemeColors
import dev.msfjarvis.claw.common.theme.LightThemeColors
import dev.msfjarvis.claw.common.theme.LobstersTheme
import dev.msfjarvis.claw.common.urllauncher.UrlLauncher
import dev.msfjarvis.claw.database.local.SavedPost
import org.pushingpixels.aurora.component.AuroraVerticalScrollbar
import org.pushingpixels.aurora.theming.ceruleanSkin
import org.pushingpixels.aurora.window.AuroraWindow
import org.pushingpixels.aurora.window.auroraApplication

fun main() = auroraApplication {
  val api = Api()
  val paging = Paging(rememberCoroutineScope(), api.api::getHottestPosts)
  val items = paging.pagingData.collectAsLazyPagingItems()
  val urlLauncher = UrlLauncher()
  val state =
    rememberWindowState(
      placement = WindowPlacement.Floating,
      position = WindowPosition.Aligned(Alignment.Center),
    )
  val postActions = remember {
    object : PostActions {
      override fun viewPost(postUrl: String, commentsUrl: String) {
        urlLauncher.openUri(postUrl.ifEmpty { commentsUrl })
      }

      override fun viewComments(postId: String) {
        urlLauncher.openUri("${LobstersApi.BASE_URL}/s/${postId}")
      }

      override fun viewCommentsPage(commentsUrl: String) {
        urlLauncher.openUri(commentsUrl)
      }

      override fun toggleSave(post: SavedPost) {}
    }
  }
  AuroraWindow(
    skin = ceruleanSkin(),
    title = "Claw",
    state = state,
    undecorated = true,
    onCloseRequest = ::exitApplication,
  ) {
    val colorScheme =
      if (isSystemInDarkTheme()) {
        DarkThemeColors
      } else {
        LightThemeColors
      }
    LobstersTheme(
      colorScheme = colorScheme,
      providedValues = arrayOf(LocalUriHandler provides urlLauncher),
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
      ) {
        val listState = rememberLazyListState()
        if (items.itemCount == 0) {
          Box(modifier = Modifier.fillMaxSize())
        } else {
          LazyColumn(
            state = listState,
          ) {
            items(items) { item ->
              if (item != null) {
                LobstersCard(
                  post = item.toDbModel(),
                  isSaved = false,
                  postActions = postActions,
                  modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                )
              }
            }
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
