package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import org.junit.Rule
import org.junit.Test

class LobstersItemTest : ScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun postsAreRenderedCorrectlyOnScreen() {
    composeTestRule.setContent {
      LobstersTheme {
        LobstersItem(
          post = TEST_POST,
          onClick = { /*TODO*/ },
          onLongClick = { /*TODO*/ },
          onSaveButtonClick = { /*TODO*/ },
          isSaved = true,
        )
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
