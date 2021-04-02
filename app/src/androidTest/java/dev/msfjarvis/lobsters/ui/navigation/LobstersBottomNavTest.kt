package dev.msfjarvis.lobsters.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.DarkTestTheme
import dev.msfjarvis.lobsters.ui.main.LobstersBottomNav
import kotlin.test.Test
import org.junit.Ignore
import org.junit.Rule

@Ignore("Shot is broken yet again")
class LobstersBottomNavTest : ScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bottomNavIsRenderedCorrectlyOnScreen() {
    composeTestRule.setContent {
      DarkTestTheme {
        LobstersBottomNav(
          currentDestination = Destination.startDestination,
          navigateToDestination = {},
          jumpToIndex = { _, _ -> },
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun bottomNavUpdatesCorrectly() {
    composeTestRule.setContent {
      DarkTestTheme {
        var destination by remember { mutableStateOf(Destination.startDestination) }

        LobstersBottomNav(
          currentDestination = destination,
          navigateToDestination = { newDestination -> destination = newDestination },
          jumpToIndex = { _, _ -> },
        )
      }
    }

    selectNode(Destination.Saved.name)

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  private fun selectNode(testTag: String) =
    composeTestRule.onNodeWithTag(testTag).assertHasClickAction().performClick()
}
