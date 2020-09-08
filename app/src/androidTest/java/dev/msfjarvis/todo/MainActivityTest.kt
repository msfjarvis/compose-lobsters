package dev.msfjarvis.todo

import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import androidx.ui.test.performClick
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
}
