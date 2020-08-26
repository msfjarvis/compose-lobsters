package dev.msfjarvis.todo

import androidx.ui.test.android.createAndroidComposeRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.onNodeWithText
import dev.msfjarvis.todo.ui.TodoTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>(disableTransitions = true)

  @Before
  fun setUp() {
    composeTestRule.setContent {
      TodoTheme {
        TodoApp()
      }
    }
  }

  @Test
  fun test_default_item() {
    onNodeWithText("Default item").assertIsDisplayed()
  }
}
