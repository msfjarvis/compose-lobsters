package dev.msfjarvis.lobsters.ui.navigation

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.main.LobstersBottomNav
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import org.junit.Rule
import org.junit.Test

class LobstersBottomNavTest : ScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun bottomNavIsRenderedCorrectlyOnScreen() {
    composeTestRule.setContent {
      LobstersTheme {
        LobstersBottomNav(
          currentDestination = Destination.startDestination,
          navigateToDestination = { /*TODO*/ },
          jumpToIndex = { /*TODO*/ }
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
