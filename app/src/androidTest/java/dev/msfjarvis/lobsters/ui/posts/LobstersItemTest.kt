package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.karumi.shot.ScreenshotTest
import dev.msfjarvis.lobsters.ui.DarkTestTheme
import dev.msfjarvis.lobsters.ui.LightTestTheme
import kotlin.test.Test
import org.junit.Rule

class LobstersItemTest : ScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun singlePost() {
    composeTestRule.setContent {
      DarkTestTheme {
        LobstersItem(
          post = TEST_POST,
          viewPost = {},
          viewComments = {},
          toggleSave = {},
          isSaved = true,
        )
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun multiplePosts() {
    composeTestRule.setContent {
      LightTestTheme {
        LazyColumn {
          items(10) {
            LobstersItem(
              post = TEST_POST,
              viewPost = {},
              viewComments = {},
              toggleSave = {},
              isSaved = true,
            )
          }
        }
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }

  @Test
  fun multiplePostsWithLesserTags() {
    composeTestRule.setContent {
      LightTestTheme {
        LazyColumn {
          items(10) {
            LobstersItem(
              post = TEST_POST.copy(tags = listOf("openbsd", "linux")),
              viewPost = {},
              viewComments = {},
              toggleSave = {},
              isSaved = true,
            )
          }
        }
      }
    }
    compareScreenshot(composeTestRule.onRoot().captureToImage().asAndroidBitmap())
  }
}
