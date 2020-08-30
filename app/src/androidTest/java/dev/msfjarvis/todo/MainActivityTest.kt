package dev.msfjarvis.todo

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithText
import dev.msfjarvis.todo.data.model.TodoItem
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
        val items = arrayListOf(TodoItem("Item 1"))
        TodoApp(
          items,
          items::add,
          items::remove,
        )
      }
    }
    //onNode(hasTestTag("fab")).performClick()
    onNodeWithText("Item 1").assertIsDisplayed()
  }
}
