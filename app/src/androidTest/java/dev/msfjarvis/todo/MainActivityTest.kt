package dev.msfjarvis.todo

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.hasTestTag
import androidx.ui.test.onNode
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import dev.msfjarvis.todo.ui.TodoTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun test_item_addition() {
    composeTestRule.setContent {
      TodoTheme {
        TodoApp()
      }
    }
    onNode(hasTestTag("fab")).performClick()
    onNodeWithText("Item 1").assertIsDisplayed()
  }
}
