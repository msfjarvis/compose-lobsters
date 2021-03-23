package dev.msfjarvis.lobsters.ui.main

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.DarkTestTheme
import dev.msfjarvis.lobsters.ui.LightTestTheme
import dev.msfjarvis.lobsters.ui.navigation.Destination
import org.junit.Rule
import org.junit.Test

class LobstersTopBarTest : ScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun showsRefreshIconWhenOnHottestPostsScreen_DarkTheme() {
    composeTestRule.setContent {
      DarkTestTheme {
        LobstersTopAppBar(
          currentDestination = Destination.Hottest,
          toggleSortingOrder = { },
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun showsRefreshIconWhenOnHottestPostsScreen_LightTheme() {
    composeTestRule.setContent {
      LightTestTheme {
        LobstersTopAppBar(
          currentDestination = Destination.Hottest,
          toggleSortingOrder = { },
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun doesNotShowRefreshIconWhenOnSavedPostsScreen_DarkTheme() {
    composeTestRule.setContent {
      DarkTestTheme {
        LobstersTopAppBar(
          currentDestination = Destination.Saved,
          toggleSortingOrder = { },
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun doesNotShowRefreshIconWhenOnSavedPostsScreen_LightTheme() {
    composeTestRule.setContent {
      LightTestTheme {
        LobstersTopAppBar(
          currentDestination = Destination.Saved,
          toggleSortingOrder = { },
        )
      }
    }

    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
