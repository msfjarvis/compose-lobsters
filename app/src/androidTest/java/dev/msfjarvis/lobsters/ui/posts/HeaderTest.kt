package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.DarkTestTheme
import java.time.Month
import org.junit.Rule
import org.junit.Test

class HeaderTest : ScreenshotTest {
  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun headerDoesNotHaveATransparentBackground() {
    composeTestRule.setContent {
      DarkTestTheme {
        Box(
          modifier = Modifier
            .background(color = Color(0xffffff))
            .fillMaxWidth()
            .wrapContentHeight(),
        ) {
          MonthHeader(month = Month.AUGUST)
        }
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
