package dev.msfjarvis.lobsters.ui.posts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.msfjarvis.lobsters.ui.theme.LobstersTheme
import org.junit.Rule
import org.junit.Test

class LobstersItemTest {
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

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText(TEST_POST.title).assertIsDisplayed()
  }
}
