package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.DarkTestTheme
import kotlin.test.Test
import org.junit.Rule

class LobstersItemTest : ScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun postsAreRenderedCorrectlyOnScreen() {
    composeTestRule.setContent {
      DarkTestTheme {
        LobstersItem(
          post = TEST_POST,
          viewPost = { /*TODO*/ },
          viewComments = { /*TODO*/ },
          toggleSave = { /*TODO*/ },
          isSaved = true,
        )
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
