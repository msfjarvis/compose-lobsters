package dev.msfjarvis.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.onNodeWithText
import androidx.ui.test.performClick
import androidx.ui.test.performTextInput
import dev.msfjarvis.todo.data.model.TodoItem
import dev.msfjarvis.todo.ui.TodoTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun item_add_dialog_shows_on_fab_click() {
    composeTestRule.setContent {
      TodoTheme {
        val items = arrayListOf<TodoItem>()
        TodoApp(
          items,
          items::add,
          items::remove,
        )
      }
    }
    onNodeWithTag("fab").performClick()
    onNodeWithTag("item_dialog").assertIsDisplayed()
  }

  @Test
  fun item_addition_adds_new_entry() {
    composeTestRule.setContent {
      val items by mutableStateOf(arrayListOf<TodoItem>())
      TodoTheme {
        TodoApp(
          items,
          items::add,
          items::remove,
        )
      }
    }
    onNodeWithText("Item 1").assertDoesNotExist()
    onNodeWithTag("fab").performClick()
    onNodeWithTag("item_name").performTextInput("Item 1")
    onNodeWithTag("add_button").performClick()
    onNodeWithText("Item 1").assertIsDisplayed()
  }
}
