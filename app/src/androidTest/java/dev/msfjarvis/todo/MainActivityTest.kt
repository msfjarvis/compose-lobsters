package dev.msfjarvis.todo

import androidx.ui.test.android.createAndroidComposeRule
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.hasTestTag
import androidx.ui.test.onNode
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import dev.msfjarvis.todo.ui.TodoTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>(disableTransitions = true)

  @Test
  fun test_item_addition() {
    composeTestRule.setContent {
      TodoTheme {
        TodoApp()
      }
    }
    onNode(hasTestTag("fab")).performClick()
    onNodeWithText("Item 2").assertIsDisplayed()
  }
}
